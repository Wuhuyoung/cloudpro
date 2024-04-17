package com.cloud.pro.server.modules.service;

import com.cloud.pro.server.modules.context.file.DeleteFileContext;
import com.cloud.pro.server.modules.context.file.QueryFileListContext;
import com.cloud.pro.server.modules.context.file.SecUploadFileContext;
import com.cloud.pro.server.modules.context.file.UpdateFilenameContext;
import com.cloud.pro.server.modules.context.user.CreateFolderContext;
import com.cloud.pro.server.modules.entity.UserFile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.pro.server.modules.vo.UserFileVO;

import java.util.List;

/**
* @author han
* @description 针对表【cloud_pro_user_file(用户文件信息表)】的数据库操作Service
* @createDate 2024-02-26 23:00:21
*/
public interface UserFileService extends IService<UserFile> {
    /**
     * 创建文件夹信息
     * @param createFolderContext
     * @return 文件id
     */
    Long createFolder(CreateFolderContext createFolderContext);

    /**
     * 查询用户的根文件夹信息
     * @param userId
     * @return
     */
    UserFile getUserRootFile(Long userId);

    /**
     * 查询用户文件列表
     * @param queryFileListContext
     * @return
     */
    List<UserFileVO> getFileList(QueryFileListContext queryFileListContext);

    /**
     * 文件重命名
     * @param context
     */
    void updateFilename(UpdateFilenameContext context);

    /**
     * 批量删除文件
     * @param deleteFileContext
     */
    void deleteFile(DeleteFileContext deleteFileContext);

    /**
     * 文件秒传
     * @param secUploadFileContext
     * @return
     */
    boolean secUpload(SecUploadFileContext secUploadFileContext);
}
