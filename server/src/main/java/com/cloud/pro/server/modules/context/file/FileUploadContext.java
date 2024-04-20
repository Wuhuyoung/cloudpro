package com.cloud.pro.server.modules.context.file;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 单文件上传 上下文实体
 */
@Data
public class FileUploadContext implements Serializable {
    private static final long serialVersionUID = 4933278628332163430L;

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
     * 父文件ID
     */
    private Long parentId;

    /**
     * 要上传的文件实体
     */
    private MultipartFile file;

    /**
     * 当前登录用户ID
     */
    private Long userId;
}
