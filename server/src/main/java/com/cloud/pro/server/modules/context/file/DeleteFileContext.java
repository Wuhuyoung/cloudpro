package com.cloud.pro.server.modules.context.file;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量删除文件 上下文实体
 */
@Data
public class DeleteFileContext implements Serializable {
    private static final long serialVersionUID = -3945619456412929921L;
    /**
     * 要删除的文件ID集合
     */
    private List<Long> fileIdList;
    /**
     * 当前登录用户ID
     */
    private Long userId;
}
