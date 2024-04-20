package com.cloud.pro.server.modules.context.file;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询用户已上传的文件分片列表 上下文实体
 */
@Data
public class QueryUploadedChunksContext implements Serializable {
    private static final long serialVersionUID = 1429503900447749381L;

    /**
     * 文件的唯一标识
     */
    private String identifier;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
