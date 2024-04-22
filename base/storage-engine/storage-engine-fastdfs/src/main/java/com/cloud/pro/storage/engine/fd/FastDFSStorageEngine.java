package com.cloud.pro.storage.engine.fd;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.pro.core.constants.CommonConstants;
import com.cloud.pro.core.exception.FrameworkException;
import com.cloud.pro.core.utils.FileUtil;
import com.cloud.pro.storage.engine.core.AbstractStorageEngine;
import com.cloud.pro.storage.engine.core.context.DeleteFileContext;
import com.cloud.pro.storage.engine.core.context.MergeFileContext;
import com.cloud.pro.storage.engine.core.context.ReadFileContext;
import com.cloud.pro.storage.engine.core.context.StoreFileChunkContext;
import com.cloud.pro.storage.engine.core.context.StoreFileContext;
import com.cloud.pro.storage.engine.fd.config.FastDFSStorageEngineConfig;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * 基于FastDFS的文件存储引擎
 */
@Component
public class FastDFSStorageEngine extends AbstractStorageEngine {
    @Resource
    private FastFileStorageClient client;

    @Resource
    private FastDFSStorageEngineConfig config;

    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        StorePath storePath = client.uploadFile(config.getGroup(),
                context.getInputStream(),
                context.getTotalSize(),
                FileUtil.getFileExtName(context.getFilename()));
        context.setRealPath(storePath.getFullPath());
    }

    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {
        List<String> realFilePathList = context.getRealFilePathList();
        if (CollectionUtil.isNotEmpty(realFilePathList)) {
            realFilePathList.forEach(realPath -> client.deleteFile(realPath));
        }
    }

    @Override
    protected void doStoreChunk(StoreFileChunkContext context) throws IOException {
        throw new FrameworkException("FastDFS不支持分片上传的操作");
    }

    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {
        throw new FrameworkException("FastDFS不支持分片上传的操作");
    }

    @Override
    protected void doReadFile(ReadFileContext context) throws IOException {
        String realPath = context.getRealPath();
        String group = realPath.substring(CommonConstants.ZERO_INT, realPath.indexOf(CommonConstants.SLASH_STR));
        String path = realPath.substring(realPath.indexOf(CommonConstants.SLASH_STR) + CommonConstants.ONE_INT);

        DownloadByteArray downloadByteArray = new DownloadByteArray();
        byte[] bytes = client.downloadFile(group, path, downloadByteArray);

        OutputStream outputStream = context.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }
}
