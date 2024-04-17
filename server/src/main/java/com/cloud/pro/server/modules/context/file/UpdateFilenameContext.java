package com.cloud.pro.server.modules.context.file;

import com.cloud.pro.server.modules.entity.UserFile;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新文件上下文实体
 */
@Data
public class UpdateFilenameContext implements Serializable {
    private static final long serialVersionUID = 4124969934025213308L;
    /**
     * 要更新的文件ID
     */
    private Long fileId;

    /**
     * 新的文件名称
     */
    private String newFilename;

    /**
     * 当前的登录用户ID
     */
    private Long userId;

    /**
     * 要更新的文件记录实体
     */
    private UserFile userFile;
}
