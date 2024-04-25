package com.cloud.pro.server.modules.context.share;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 取消分享 上下文实体
 */
@Data
public class CancelShareContext implements Serializable {
    private static final long serialVersionUID = 9218550200763203311L;

    /**
     * 要取消的分享ID的集合
     */
    private List<Long> shareIdList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
