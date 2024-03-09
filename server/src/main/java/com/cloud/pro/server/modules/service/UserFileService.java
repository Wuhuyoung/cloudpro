package com.cloud.pro.server.modules.service;

import com.cloud.pro.server.modules.context.CreateFolderContext;
import com.cloud.pro.server.modules.entity.UserFile;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
