package com.cloud.pro.server.modules.context.file;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文件搜索 上下文实体
 */
@Data
public class FileSearchContext implements Serializable {
    private static final long serialVersionUID = 980834765268334521L;

    /**
     * 搜索的关键字
     */
    private String keyword;

    /**
     * 搜索的文件类型集合
     */
    private List<Integer> fileTypeArray;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
