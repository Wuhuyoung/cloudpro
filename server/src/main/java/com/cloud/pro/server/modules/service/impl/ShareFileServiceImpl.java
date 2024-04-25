package com.cloud.pro.server.modules.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.pro.core.exception.BusinessException;
import com.cloud.pro.core.utils.IdUtil;
import com.cloud.pro.server.modules.context.share.SaveShareFilesContext;
import com.cloud.pro.server.modules.entity.ShareFile;
import com.cloud.pro.server.modules.service.ShareFileService;
import com.cloud.pro.server.modules.mapper.ShareFileMapper;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author han
* @description 针对表【cloud_pro_share_file(用户分享文件表)】的数据库操作Service实现
* @createDate 2024-04-23 22:26:06
*/
@Service
public class ShareFileServiceImpl extends ServiceImpl<ShareFileMapper, ShareFile> implements ShareFileService {

    /**
     * 保存分享和对应文件的关联关系
     * @param context
     */
    @Override
    public void saveShareFiles(SaveShareFilesContext context) {
        Long shareId = context.getShareId();
        List<Long> shareFileIdList = context.getShareFileIdList();
        Long userId = context.getUserId();
        List<ShareFile> shareFileList = Lists.newArrayList();

        for (Long fileId : shareFileIdList) {
            ShareFile shareFile = new ShareFile();
            shareFile.setId(IdUtil.get());
            shareFile.setShareId(shareId);
            shareFile.setFileId(fileId);
            shareFile.setCreateUser(userId);
            shareFileList.add(shareFile);
        }
        if (!this.saveBatch(shareFileList)) {
            throw new BusinessException("保存文件分享关联关系失败");
        }
    }
}




