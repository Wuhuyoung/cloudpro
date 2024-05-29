package com.cloud.pro.server.modules.context.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询用户搜索历史 上下文实体
 */
@Data
public class QueryUserSearchHistoryContext implements Serializable {
    private static final long serialVersionUID = -4437551634553646295L;

    /**
     * 当前登录用户的ID
     */
    private Long userId;
}
