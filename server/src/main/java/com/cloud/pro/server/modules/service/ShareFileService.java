package com.cloud.pro.server.modules.service;

import com.cloud.pro.server.modules.context.share.SaveShareFilesContext;
import com.cloud.pro.server.modules.entity.ShareFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author han
* @description 针对表【cloud_pro_share_file(用户分享文件表)】的数据库操作Service
* @createDate 2024-04-23 22:26:06
*/
public interface ShareFileService extends IService<ShareFile> {

    /**
     * 保存分享和对应文件的关联关系
     * @param context
     */
    void saveShareFiles(SaveShareFilesContext context);
}
