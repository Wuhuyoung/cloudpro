package com.cloud.pro.server.modules.context.file;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询文件夹树 上下文实体
 */
@Data
public class QueryFolderTreeContext implements Serializable {
    private static final long serialVersionUID = 3832142339321449498L;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
