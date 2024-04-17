package com.cloud.pro.server.modules.context.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建文件夹上下文实体
 */
@Data
public class CreateFolderContext implements Serializable {
    private static final long serialVersionUID = 2742079881044069148L;
    /**
     * 父文件ID
     */
    private Long parentId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 文件夹名称
     */
    private String folderName;
}
