package com.cloud.pro.server.modules.context.recycle;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询回收站文件列表 上下文实体
 */
@Data
public class QueryRecycleFileListContext implements Serializable {
    private static final long serialVersionUID = 3705065458718867948L;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
