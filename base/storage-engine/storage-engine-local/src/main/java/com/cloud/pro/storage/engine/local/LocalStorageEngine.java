package com.cloud.pro.storage.engine.local;

import com.cloud.pro.core.utils.FileUtil;
import com.cloud.pro.storage.engine.core.AbstractStorageEngine;
import com.cloud.pro.storage.engine.core.context.DeleteFileContext;
import com.cloud.pro.storage.engine.core.context.MergeFileContext;
import com.cloud.pro.storage.engine.core.context.StoreFileChunkContext;
import com.cloud.pro.storage.engine.core.context.StoreFileContext;
import com.cloud.pro.storage.engine.local.config.LocalStorageEngineConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * 本地文件存储引擎
 */
@Component
public class LocalStorageEngine extends AbstractStorageEngine {
    @Resource
    private LocalStorageEngineConfig config;

    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        // 生成合并后的文件路径
        String basePath = config.getRootFilePath();
        String realFilePath = FileUtil.generateStoreFileRealPath(basePath, context.getFilename());
        // 通过零拷贝写文件
        FileUtil.writeStream2File(context.getInputStream(), new File(realFilePath), context.getTotalSize());

        context.setRealPath(realFilePath);
    }

    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {
        FileUtil.deleteFiles(context.getRealFilePathList());
    }

    @Override
    protected void doStoreChunk(StoreFileChunkContext context) throws IOException {
        // 生成该分片的路径
        String basePath = config.getRootFileChunkPath();
        String realFilePath = FileUtil.generateStoreFileChunkRealPath(basePath, context.getIdentifier(), context.getChunkNumber());
        // 通过零拷贝写文件
        FileUtil.writeStream2File(context.getInputStream(), new File(realFilePath), context.getTotalSize());

        context.setRealPath(realFilePath);
    }

    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {
        // 生成合并后的文件路径
        String basePath = config.getRootFilePath();
        String realFilePath = FileUtil.generateStoreFileRealPath(basePath, context.getFilename());
        // 遍历每一个分片，追加写入
        FileUtil.createFile(new File(realFilePath));
        List<String> chunkPaths = context.getRealPathList();
        for (String chunkPath : chunkPaths) {
            // tip:通用操作不涉及业务逻辑，可以抽取到工具类中
            FileUtil.appendWrite(Paths.get(realFilePath), new File(chunkPath).toPath());
        }
        // 删除分片
        FileUtil.deleteFiles(chunkPaths);

        context.setRealPath(realFilePath);
    }
}
