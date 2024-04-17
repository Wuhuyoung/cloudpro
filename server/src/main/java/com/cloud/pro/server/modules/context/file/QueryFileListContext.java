package com.cloud.pro.server.modules.context.file;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 查询文件列表上下文实体
 */
@Data
public class QueryFileListContext implements Serializable {
    private static final long serialVersionUID = 6235327929325074562L;

    /**
     * 父文件夹ID
     */
    private Long parentId;

    /**
     * 当前登录用户ID
     */
    private Long userId;

    /**
     * 文件类型的集合
     */
    private List<Integer> fileTypeArray;

    /**
     * 删除标识
     */
    private Integer delFlag;
}
