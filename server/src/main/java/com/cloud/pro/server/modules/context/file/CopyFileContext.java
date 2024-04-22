package com.cloud.pro.server.modules.context.file;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文件复制 上下文实体
 */
@Data
public class CopyFileContext implements Serializable {
    private static final long serialVersionUID = -345473091540694852L;

    /**
     * 要复制的文件ID集合
     */
    private List<Long> fileIdList;

    /**
     * 目标文件夹ID
     */
    private Long targetParentId;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
