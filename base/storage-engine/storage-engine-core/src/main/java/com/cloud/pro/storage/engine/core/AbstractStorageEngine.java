package com.cloud.pro.storage.engine.core;

import com.cloud.pro.storage.engine.core.context.DeleteFileContext;
import com.cloud.pro.storage.engine.core.context.MergeFileContext;
import com.cloud.pro.storage.engine.core.context.ReadFileContext;
import com.cloud.pro.storage.engine.core.context.StoreFileChunkContext;
import com.cloud.pro.storage.engine.core.context.StoreFileContext;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;

/**
 * 文件存储引擎
 */
public abstract class AbstractStorageEngine implements StorageEngine {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 共用的获取缓存的方法
     * @return
     */
    protected RedisTemplate<String, Object> getCache() {
        return redisTemplate;
    }

    @Override
    public void store(StoreFileContext context) throws IOException {
        // 参数校验
        Assertions.assertTrue(StringUtils.isNotBlank(context.getFilename()), "文件名称不能为空");
        Assertions.assertTrue(Objects.nonNull(context.getTotalSize()), "文件大小不能为空");
        Assertions.assertTrue(Objects.nonNull(context.getInputStream()), "文件不能为空");
        // 保存文件
        doStore(context);
    }

    /**
     * 执行保存物理文件
     * 由具体子类调用不同存储引擎实现
     * @param context
     * @throws IOException
     */
    protected abstract void doStore(StoreFileContext context) throws IOException;

    @Override
    public void delete(DeleteFileContext context) throws IOException {
        // 参数校验
        Assertions.assertFalse(CollectionUtils.isEmpty(context.getRealFilePathList()), "要删除的文件路径列表不能为空");
        // 删除文件
        doDelete(context);
    }

    /**
     * 执行删除物理文件
     * 由具体子类调用不同存储引擎实现
     * @param context
     * @throws IOException
     */
    protected abstract void doDelete(DeleteFileContext context) throws IOException;

    @Override
    public void storeChunk(StoreFileChunkContext context) throws IOException {
        // 1.参数校验
        Assertions.assertTrue(StringUtils.isNotBlank(context.getFilename()), "文件名称不能为空");
        Assertions.assertTrue(StringUtils.isNotBlank(context.getIdentifier()), "文件唯一标识不能为空");
        Assertions.assertTrue(Objects.nonNull(context.getTotalSize()), "文件大小不能为空");
        Assertions.assertTrue(Objects.nonNull(context.getInputStream()), "文件分片不能为空");
        Assertions.assertTrue(Objects.nonNull(context.getTotalChunks()), "文件分片总数不能为空");
        Assertions.assertTrue(Objects.nonNull(context.getChunkNumber()), "文件分片下标不能为空");
        Assertions.assertTrue(Objects.nonNull(context.getCurrentChunkSize()), "文件分片的大小不能为空");
        Assertions.assertTrue(Objects.nonNull(context.getUserId()), "当前登录用户的ID不能为空");
        // 2.保存文件分片
        doStoreChunk(context);
    }

    /**
     * 保存文件分片，由具体子类调用不同存储引擎实现
     * @param context
     * @throws IOException
     */
    protected abstract void doStoreChunk(StoreFileChunkContext context) throws IOException;

    /**
     * 合并文件分片
     * @param context
     * @throws IOException
     */
    @Override
    public void mergeFile(MergeFileContext context) throws IOException {
        // 1.参数校验
        Assertions.assertTrue(StringUtils.isNotBlank(context.getFilename()), "文件名称不能为空");
        Assertions.assertTrue(StringUtils.isNotBlank(context.getIdentifier()), "文件的唯一标识不能为空");
        Assertions.assertTrue(Objects.nonNull(context.getUserId()), "登录用户ID不能为空");
        Assertions.assertTrue(!CollectionUtils.isEmpty(context.getRealPathList()), "文件分片列表不能为空");

        // 2.合并分片
        doMergeFile(context);
    }

    /**
     * 执行文件分片，由具体子类调用不同存储引擎实现
     * @param context
     * @throws IOException
     */
    protected abstract void doMergeFile(MergeFileContext context) throws IOException;

    /**
     * 读取文件内容写入到输出流中
     * @param context
     * @throws IOException
     */
    @Override
    public void readFile(ReadFileContext context) throws IOException {
        // 1.参数校验
        Assertions.assertTrue(StringUtils.isNotBlank(context.getRealPath()), "文件的真实存储路径不能为空");
        Assertions.assertTrue(Objects.nonNull(context.getOutputStream()), "输出流不能为空");
        // 2.执行写入操作
        doReadFile(context);
    }

    /**
     * 执行文件写入输出流中，，由具体子类调用不同存储引擎实现
     * @param context
     * @throws IOException
     */
    protected abstract void doReadFile(ReadFileContext context) throws IOException;
}
