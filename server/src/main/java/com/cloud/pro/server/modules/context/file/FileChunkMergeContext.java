package com.cloud.pro.server.modules.context.file;

import com.cloud.pro.server.modules.entity.File;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件分片合并 上下文实体
 */
@Data
public class FileChunkMergeContext implements Serializable {
    private static final long serialVersionUID = -8022865533194292251L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件的唯一标识
     */
    private String identifier;

    /**
     * 文件的总大小
     */
    private Long totalSize;

    /**
     * 父文件夹ID
     */
    private Long parentId;

    /**
     * 当前登录用户ID
     */
    private Long userId;

    /**
     * 物理文件记录
     */
    private File record;

    /**
     * 文件合并之后存储的真实的物理路径
     */
    private String realPath;
}
