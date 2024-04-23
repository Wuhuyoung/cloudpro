package com.cloud.pro.storage.engine.oss;

import cn.hutool.core.date.DateUtil;
import com.aliyun.oss.OSSClient;
import com.cloud.pro.core.constants.CommonConstants;
import com.cloud.pro.core.utils.FileUtil;
import com.cloud.pro.core.utils.UUIDUtil;
import com.cloud.pro.storage.engine.core.AbstractStorageEngine;
import com.cloud.pro.storage.engine.core.context.DeleteFileContext;
import com.cloud.pro.storage.engine.core.context.MergeFileContext;
import com.cloud.pro.storage.engine.core.context.ReadFileContext;
import com.cloud.pro.storage.engine.core.context.StoreFileChunkContext;
import com.cloud.pro.storage.engine.core.context.StoreFileContext;
import com.cloud.pro.storage.engine.oss.config.OSSStorageEngineConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 基于OSS的文件存储引擎
 */
@Component
public class OSSStorageEngine extends AbstractStorageEngine {
    @Resource
    private OSSStorageEngineConfig config;

    @Resource
    private OSSClient ossClient;

    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        String realPath = getFilePath(FileUtil.getFileSuffix(context.getFilename()));
        ossClient.putObject(config.getBucketName(), realPath, context.getInputStream());
        context.setRealPath(realPath);
    }

    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {

    }

    @Override
    protected void doStoreChunk(StoreFileChunkContext context) throws IOException {

    }

    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {

    }

    @Override
    protected void doReadFile(ReadFileContext context) throws IOException {

    }

    /**********************************private**********************************/

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
