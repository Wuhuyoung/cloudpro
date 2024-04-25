package com.cloud.pro.server.modules.service;

import com.cloud.pro.server.modules.context.share.CancelShareContext;
import com.cloud.pro.server.modules.context.share.CheckShareCodeContext;
import com.cloud.pro.server.modules.context.share.CreateShareUrlContext;
import com.cloud.pro.server.modules.context.share.QueryChildFileListContext;
import com.cloud.pro.server.modules.context.share.QueryShareDetailContext;
import com.cloud.pro.server.modules.context.share.QueryShareListContext;
import com.cloud.pro.server.modules.context.share.QueryShareSimpleDetailContext;
import com.cloud.pro.server.modules.context.share.ShareFileDownloadContext;
import com.cloud.pro.server.modules.context.share.ShareSaveContext;
import com.cloud.pro.server.modules.entity.Share;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.pro.server.modules.vo.ShareDetailVO;
import com.cloud.pro.server.modules.vo.ShareSimpleDetailVO;
import com.cloud.pro.server.modules.vo.ShareUrlListVO;
import com.cloud.pro.server.modules.vo.ShareUrlVO;
import com.cloud.pro.server.modules.vo.UserFileVO;

import java.util.List;

/**
* @author han
* @description 针对表【cloud_pro_share(用户分享表)】的数据库操作Service
* @createDate 2024-04-23 22:23:32
*/
public interface ShareService extends IService<Share> {

    /**
     * 创建分享链接
     * @param context
     * @return
     */
    ShareUrlVO create(CreateShareUrlContext context);

    /**
     * 查询用户的分享列表
     * @param context
     * @return
     */
    List<ShareUrlListVO> getShares(QueryShareListContext context);

    /**
     * 取消分享
     * @param context
     */
    void cancelShare(CancelShareContext context);

    /**
     * 校验分享码
     * @param context
     * @return
     */
    String checkShareCode(CheckShareCodeContext context);

    /**
     * 查询分享详情
     * @param context
     * @return
     */
    ShareDetailVO detail(QueryShareDetailContext context);

    /**
     * 查询分享的简单详情
     * @param context
     * @return
     */
    ShareSimpleDetailVO simpleDetail(QueryShareSimpleDetailContext context);

    /**
     * 获取下一级文件列表
     * @param context
     * @return
     */
    List<UserFileVO> fileList(QueryChildFileListContext context);

    /**
     * 保存文件到我的文件夹
     * @param context
     */
    void saveFiles(ShareSaveContext context);

    /**
     * 分享文件下载
     * @param context
     */
    void download(ShareFileDownloadContext context);

    /**
     * 刷新受影响的对应的分享状态
     * @param fileIdList
     */
    void refreshShareStatus(List<Long> fileIdList);
}
