package com.cloud.pro.server.modules.context.share;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 保存分享和对应文件的关联关系 上下文对象
 */
@Data
public class SaveShareFilesContext implements Serializable {
    private static final long serialVersionUID = -6358978586856933757L;

    /**
     * 分享ID
     */
    private Long shareId;

    /**
     * 分享对应的文件ID集合
     */
    private List<Long> shareFileIdList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
