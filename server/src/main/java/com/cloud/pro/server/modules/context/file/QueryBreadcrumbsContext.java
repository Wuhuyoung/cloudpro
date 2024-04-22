package com.cloud.pro.server.modules.context.file;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询面包屑列表 上下文实体
 */
@Data
public class QueryBreadcrumbsContext implements Serializable {
    private static final long serialVersionUID = 7814605907054187015L;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
