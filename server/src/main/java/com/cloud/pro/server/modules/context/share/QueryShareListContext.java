package com.cloud.pro.server.modules.context.share;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询分享列表 上下文实体
 */
@Data
public class QueryShareListContext implements Serializable {
    private static final long serialVersionUID = 5440876273575346559L;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
