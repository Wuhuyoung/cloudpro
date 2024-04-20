package com.cloud.pro.server.modules.context.file;

import com.cloud.pro.server.enums.MergeFlagEnum;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 文件分片保存 上下文实体
 */
@Data
public class FileChunkSaveContext implements Serializable {
    private static final long serialVersionUID = -2791265534491337173L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件的唯一标识
     */
    private String identifier;

    /**
     * 总共的分片数
     */
    private Integer totalChunks;

    /**
     * 当前分片的下标
     * 从1开始
     */
    private Integer chunkNumber;

    /**
     * 当前分片的大小
     */
    private Long currentChunkSize;

    /**
     * 文件总大小
     */
    private Long totalSize;

    /**
     * 分片文件实体
     */
    private MultipartFile file;

    /**
     * 当前登录用户ID
     */
    private Long userId;

    /**
     * 文件合并标识
     */
    private MergeFlagEnum mergeFlagEnum = MergeFlagEnum.NOT_READY;

    /**
     * 文件分片的真实存储路径
     */
    private String realPath;
}
