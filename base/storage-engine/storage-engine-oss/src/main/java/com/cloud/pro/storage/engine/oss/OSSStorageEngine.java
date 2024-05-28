package com.cloud.pro.storage.engine.oss;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.AbortMultipartUploadRequest;
import com.aliyun.oss.model.CompleteMultipartUploadRequest;
import com.aliyun.oss.model.CompleteMultipartUploadResult;
import com.aliyun.oss.model.InitiateMultipartUploadRequest;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PartETag;
import com.aliyun.oss.model.UploadPartRequest;
import com.aliyun.oss.model.UploadPartResult;
import com.cloud.pro.core.constants.CommonConstants;
import com.cloud.pro.core.exception.FrameworkException;
import com.cloud.pro.core.utils.FileUtil;
import com.cloud.pro.core.utils.UUIDUtil;
import com.cloud.pro.storage.engine.core.AbstractStorageEngine;
import com.cloud.pro.storage.engine.core.context.DeleteFileContext;
import com.cloud.pro.storage.engine.core.context.MergeFileContext;
import com.cloud.pro.storage.engine.core.context.ReadFileContext;
import com.cloud.pro.storage.engine.core.context.StoreFileChunkContext;
import com.cloud.pro.storage.engine.core.context.StoreFileContext;
import com.cloud.pro.storage.engine.oss.config.OSSStorageEngineConfig;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import io.jsonwebtoken.lang.Collections;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * 基于OSS的文件存储引擎
 */
@Component
public class OSSStorageEngine extends AbstractStorageEngine {
    private static final Integer TEN_THOUSAND_INT = 10000;

    private static final String CACHE_KEY_TEMPLATE = "oss:cache:upload:id:%s:%s";
    private static final String LOCK_KEY_TEMPLATE = "oss:lock:upload:%s:%s";

    private static final String IDENTIFIER_KEY = "identifier";
    private static final String UPLOAD_ID_KEY = "uploadId";
    private static final String USER_ID_KEY = "userId";
    private static final String PART_NUMBER_KEY = "partNumber";
    private static final String E_TAG_KEY = "eTag";
    private static final String PART_SIZE_KEY = "partSize";
    private static final String PART_CRC_KEY = "partCRC";

    @Resource
    private OSSStorageEngineConfig config;

    @Resource
    private OSSClient client;

    @Resource
    private LockRegistry lockRegistry;

    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        String realPath = getFilePath(FileUtil.getFileSuffix(context.getFilename()));
        client.putObject(config.getBucketName(), realPath, context.getInputStream());
        context.setRealPath(realPath);
    }

    /**
     * OSS分片上传，保存文件分片
     * 1.初始化文件分片上传，获取一个全局唯一的uploadId
     * 2.并发上传文件分片，每一个分片都需要带有uploadId
     * 3.所有分片上传完成，触发分片合并的操作
     *
     * @param context
     * @throws IOException
     */
    @Override
    protected void doStoreChunk(StoreFileChunkContext context) throws IOException {
        // 1.校验分片数不得大于10000
        if (context.getTotalChunks() > TEN_THOUSAND_INT) {
            throw new FrameworkException("分片数超过了限制，分片数不得大于" + TEN_THOUSAND_INT);
        }
        // 2.获取缓存key
        String cacheKey = getCacheKey(context.getIdentifier(), context.getUserId());
        // 3.通过缓存key获取初始化后的实例对象，获取全局的uploadId和objectName
        ChunkUploadEntity entity;
        // 4.如果获取为空，直接初始化
        // 这里优化一下，可以使用double check+分布式锁来保证只有一个线程去执行init操作
        entity = (ChunkUploadEntity) getCache().opsForValue().get(cacheKey);
        if (Objects.isNull(entity)) {
            // 锁的粒度：同一个用户上传同一份文件，只能执行一次初始化操作
            Lock lock = lockRegistry.obtain(String.format(LOCK_KEY_TEMPLATE, context.getUserId(), context.getIdentifier()));
            try {
                lock.lock();
                entity = (ChunkUploadEntity) getCache().opsForValue().get(cacheKey);
                if (Objects.isNull(entity)) {
                    entity = initChunkUpload(context.getFilename(), cacheKey);
                }
            } finally {
                lock.unlock();
            }
        }
        // 5.分片上传
        UploadPartRequest request = new UploadPartRequest();
        request.setBucketName(config.getBucketName());
        request.setKey(entity.getObjectKey());
        request.setUploadId(entity.getUploadId());
        request.setInputStream(context.getInputStream());
        request.setPartSize(context.getCurrentChunkSize());
        request.setPartNumber(context.getChunkNumber());

        UploadPartResult result = client.uploadPart(request);

        if (Objects.isNull(result)) {
            throw new FrameworkException("文件分片上传失败");
        }
        PartETag partETag = result.getPartETag();
        // 6.上传完成后，将全局的参数封装成一个可识别的url，保存在上下文里面，用于业务的落库操作
        JSONObject params = new JSONObject();
        params.put(IDENTIFIER_KEY, context.getIdentifier());
        params.put(UPLOAD_ID_KEY, entity.getUploadId());
        params.put(USER_ID_KEY, context.getUserId());
        params.put(E_TAG_KEY, partETag.getETag());
        params.put(PART_NUMBER_KEY, partETag.getPartNumber());
        params.put(PART_SIZE_KEY, partETag.getPartSize());
        params.put(PART_CRC_KEY, partETag.getPartCRC());

        String realPath = assembleUrl(entity.getObjectKey(), params);
        context.setRealPath(realPath);
    }

    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {
        // 1.从缓存中获取全局的uploadId
        String cacheKey = getCacheKey(context.getIdentifier(), context.getUserId());
        final ChunkUploadEntity[] entity = {(ChunkUploadEntity) getCache().opsForValue().get(cacheKey)};
        AtomicReference<String> baseUrl = new AtomicReference<>("");
//        if (Objects.isNull(entity)) {
//            // 这里如果缓存失效，可以从URL格式的realPath中获取请求参数uploadId和objectKey
//            throw new FrameworkException("文件分片合并请求失败，文件的唯一标识为:" + context.getIdentifier());
//        }
        // 2.从context中获取所有分片的URL，解析出文件合并请求的参数
        List<String> chunkPaths = context.getRealPathList();
        if (Collections.isEmpty(chunkPaths)) {
            throw new FrameworkException("文件分片合并请求失败，没有上传分片，文件的唯一标识为:" + context.getIdentifier());
        }
        List<PartETag> partETags = chunkPaths.stream()
                .filter(StringUtils::isNotBlank)
                .map(chunkPath -> {
                    baseUrl.set(getBaseUrl(chunkPath));
                    return analysisParams(chunkPath);
                })
                .filter(Objects::nonNull)
                .filter(jsonObject -> !jsonObject.isEmpty())
                .map(jsonObject -> {
                    if (Objects.isNull(entity[0])) {
                        // 这里如果缓存失效，可以从URL格式的realPath中获取请求参数uploadId和objectKey
                        entity[0] = new ChunkUploadEntity(jsonObject.getString(UPLOAD_ID_KEY),
                                baseUrl.get());
                    }
                    return new PartETag(jsonObject.getIntValue(PART_NUMBER_KEY),
                            jsonObject.getString(E_TAG_KEY),
                            jsonObject.getLongValue(PART_SIZE_KEY),
                            jsonObject.getLongValue(PART_CRC_KEY));
                })
                .collect(Collectors.toList());
        // 3.执行文件合并
        CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(
                config.getBucketName(), entity[0].getObjectKey(), entity[0].getUploadId(), partETags);
        CompleteMultipartUploadResult result = client.completeMultipartUpload(request);
        if (Objects.isNull(result)) {
            throw new FrameworkException("文件分片合并请求失败，文件的唯一标识为:" + context.getIdentifier());
        }
        // 4.清空缓存
        getCache().delete(cacheKey);

        context.setRealPath(entity[0].getObjectKey());
    }

    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {
        // 1.获取所有需要删除文件的路径
        List<String> realFilePathList = context.getRealFilePathList();

        realFilePathList.stream().forEach(realPath -> {
            // 2.如果该文件是分片，则从路径中解析出uploadId，然后取消文件分片的操作
            if (checkHaveParams(realPath)) {
                JSONObject params = analysisParams(realPath);
                String uploadId = params.getString(UPLOAD_ID_KEY);
                // 清空缓存
                String identifier = params.getString(IDENTIFIER_KEY);
                long userId = params.getLongValue(USER_ID_KEY);
                String cacheKey = getCacheKey(identifier, userId);
                getCache().delete(cacheKey);
                // 取消文件分片
                try {
                    AbortMultipartUploadRequest request = new AbortMultipartUploadRequest(
                            config.getBucketName(), getBaseUrl(realPath), uploadId);
                    client.abortMultipartUpload(request);
                } catch (Exception e) {
                    // 由于其他分片可能已经取消了分片上传，会抛出异常，属于正常现象
                }
            }
            // 3.如果就是一个正常的文件，直接物理删除即可
            else {
                client.deleteObject(config.getBucketName(), realPath);
            }
        });
    }

    @Override
    protected void doReadFile(ReadFileContext context) throws IOException {
        OSSObject ossObject = client.getObject(config.getBucketName(), context.getRealPath());
        if (Objects.isNull(ossObject)) {
            throw new FrameworkException("文件读取失败，文件名称为" + context.getRealPath());
        }
        FileUtil.writeStream2Stream(ossObject.getObjectContent(), context.getOutputStream());
    }

    /**********************************private**********************************/

    /**
     * 文件分片上传树池化之后的全局信息载体
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChunkUploadEntity implements Serializable {
        private static final long serialVersionUID = 1818076767741243248L;

        /**
         * 分片上传的全局唯一uploadId
         */
        private String uploadId;

        /**
         * 上传的实体名称
         */
        private String objectKey;
    }

    /**
     * 分析URL参数
     * @param url
     * @return
     */
    private JSONObject analysisParams(String url) {
        JSONObject result = new JSONObject();
        if (!checkHaveParams(url)) {
            return result;
        }
        String paramsPart = url.split(getSplitMark(CommonConstants.QUESTION_MARK_STR))[1];
        if (StringUtils.isNotBlank(paramsPart)) {
            List<String> paramPairList = Splitter.on(CommonConstants.AND_MARK_STR).splitToList(paramsPart);
            paramPairList.stream().forEach(paramPair -> {
                String[] paramArr = paramPair.split(getSplitMark(CommonConstants.EQUALS_MARK_STR));
                if (paramArr != null && paramArr.length == CommonConstants.TWO_INT) {
                    result.put(paramArr[0], paramArr[1]);
                }
            });
        }
        return result;
    }

    /**
     * 拼装URL
     * @param baseUrl
     * @param params
     * @return baseUrl?key=value&key=value
     */
    private String assembleUrl(String baseUrl, JSONObject params) {
        if (Objects.isNull(params) || params.isEmpty()) {
            return baseUrl;
        }
        StringBuilder urlStringBuffer = new StringBuilder(baseUrl);
        urlStringBuffer.append(CommonConstants.QUESTION_MARK_STR);
        List<String> paramsList = Lists.newArrayList();
        StringBuilder urlParams = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            urlParams.setLength(CommonConstants.ZERO_INT); // 为了复用该StringBuilder对象，而不用每次都创建一个新的
            urlParams.append(entry.getKey());
            urlParams.append(CommonConstants.EQUALS_MARK_STR);
            urlParams.append(entry.getValue());
            paramsList.add(urlParams.toString());
        }
        return urlStringBuffer.append(Joiner.on(CommonConstants.AND_MARK_STR).join(paramsList)).toString();
    }

    /**
     * 获取基础URL
     * @param url
     * @return
     */
    private String getBaseUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return StringUtils.EMPTY;
        }
        if (checkHaveParams(url)) {
            return url.split(getSplitMark(CommonConstants.QUESTION_MARK_STR))[0];
        }
        return url;
    }

    /**
     * 获取截取字符串的关键标识
     * 由于字符串分割会按照正则去截取，正则表达式中有11个特殊字符：^、$、|、.、*、+、?、()、{}、\、/
     * 需要对特殊字符做转义，也可以将特殊字符放在中括号中，推荐放在中括号中
     * @param mark
     * @return
     */
    private String getSplitMark(String mark) {
        return new StringBuffer(CommonConstants.LEFT_BRACKET_STR)
                .append(mark)
                .append(CommonConstants.RIGHT_BRACKET_STR)
                .toString();
    }

    /**
     * 检查是否是含有参数的URL
     * @param url
     * @return
     */
    private boolean checkHaveParams(String url) {
        return StringUtils.isNotBlank(url) && url.indexOf(CommonConstants.QUESTION_MARK_STR) != CommonConstants.MINUS_ONE_INT;
    }

    /**
     * 初始化分片上传
     * @param filename
     * @param cacheKey
     * @return
     */
    private ChunkUploadEntity initChunkUpload(String filename, String cacheKey) {
        // 执行初始化操作
        String realPath = getFilePath(FileUtil.getFileSuffix(filename));
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(config.getBucketName(), realPath);
        InitiateMultipartUploadResult result = client.initiateMultipartUpload(request);
        if (Objects.isNull(result)) {
            throw new FrameworkException("文件分片上传失败");
        }
        ChunkUploadEntity entity = new ChunkUploadEntity();
        entity.setUploadId(result.getUploadId());
        entity.setObjectKey(realPath);

        // 将初始化对象保存到缓存中
        getCache().opsForValue().set(cacheKey, entity);
        return entity;
    }

    /**
     * 获取分片上传的缓存key
     * @param identifier
     * @param userId
     * @return
     */
    private String getCacheKey(String identifier, Long userId) {
        return String.format(CACHE_KEY_TEMPLATE, identifier, userId);
    }

    /**
     * 获取对象的完整名称
     * 年/月/日/UUID.fileSuffix
     * @param fileSuffix
     * @return
     */
    private String getFilePath(String fileSuffix) {
        return new StringBuffer()
                .append(DateUtil.thisYear())
                .append(CommonConstants.SLASH_STR)
                .append(DateUtil.thisMonth() + 1)
                .append(CommonConstants.SLASH_STR)
                .append(DateUtil.thisDayOfMonth())
                .append(CommonConstants.SLASH_STR)
                .append(UUIDUtil.getUUID())
                .append(fileSuffix)
                .toString();
    }
}
