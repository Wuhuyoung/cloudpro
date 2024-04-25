package com.cloud.pro.server.modules.context.share;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 保存至我的文件夹 上下文实体
 */
@Data
public class ShareSaveContext implements Serializable {
    private static final long serialVersionUID = -6659341113568395345L;
    /**
     * 要保存的文件ID集合
     */
    private List<Long> fileIdList;

    /**
     * 要保存到的文件夹ID
     */
    private Long targetParentId;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 分享的ID
     */
    private Long shareId;
}
