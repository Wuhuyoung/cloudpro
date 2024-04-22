package com.cloud.pro.server.modules.context.file;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文件转移 上下文实体
 */
@Data
public class TransferFileContext implements Serializable {
    private static final long serialVersionUID = 7166439921600614628L;

    /**
     * 要转移的文件ID集合
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
