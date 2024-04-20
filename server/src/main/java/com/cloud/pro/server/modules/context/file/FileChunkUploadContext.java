package com.cloud.pro.server.modules.context.file;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 文件分片上传 上下文实体
 */
@Data
public class FileChunkUploadContext implements Serializable {
    private static final long serialVersionUID = 4785310029992010072L;
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
}
