package com.cloud.pro.server.modules.context.file;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文件秒传 上下文实体
 */
@Data
public class SecUploadFileContext implements Serializable {
    private static final long serialVersionUID = -2978788738915965415L;
    /**
     * 文件夹ID
     */
    private Long parentId;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
