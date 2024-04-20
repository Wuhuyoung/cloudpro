package com.cloud.pro.server.modules.service;

import com.cloud.pro.server.modules.context.file.FileChunkMergeContext;
import com.cloud.pro.server.modules.context.file.FileSaveContext;
import com.cloud.pro.server.modules.context.file.QueryRealFileListContext;
import com.cloud.pro.server.modules.entity.File;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author han
* @description 针对表【cloud_pro_file(物理文件信息表)】的数据库操作Service
* @createDate 2024-04-16 19:44:30
*/
public interface FileService extends IService<File> {

    /**
     * 根据条件查询用户的实际文件列表
     * @param context
     * @return
     */
    List<File> getFileList(QueryRealFileListContext context);

    /**
     * 上传单文件并保存实体记录
     * @param context
     */
    void saveFile(FileSaveContext context);

    /**
     * 合并文件分片，保存物理文件记录
     * @param context
     */
    void mergeFileChunkAndSaveFile(FileChunkMergeContext context);
}
