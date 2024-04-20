package com.cloud.pro.server.modules.context.file;

import com.cloud.pro.server.modules.entity.File;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 保存单文件 上下文实体
 */
@Data
public class FileSaveContext implements Serializable {
    private static final long serialVersionUID = -385423607240454125L;

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
     * 要上传的文件实体
     */
    private MultipartFile file;

    /**
     * 当前登录用户ID
     */
    private Long userId;

    /**
     * 文件上传的物理路径
     */
    private String realPath;

    /**
     * 实体文件记录
     */
    private File record;
}
