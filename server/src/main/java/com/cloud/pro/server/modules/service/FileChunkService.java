package com.cloud.pro.server.modules.service;

import com.cloud.pro.server.modules.context.file.FileChunkSaveContext;
import com.cloud.pro.server.modules.entity.FileChunk;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author han
* @description 针对表【cloud_pro_file_chunk(文件分片信息表)】的数据库操作Service
* @createDate 2024-04-19 15:19:50
*/
public interface FileChunkService extends IService<FileChunk> {

    /**
     * 文件分片保存
     * @param context
     */
    void saveChunkFile(FileChunkSaveContext context);
}
