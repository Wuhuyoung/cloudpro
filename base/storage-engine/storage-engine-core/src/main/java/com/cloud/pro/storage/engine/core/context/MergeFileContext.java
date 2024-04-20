package com.cloud.pro.storage.engine.core.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 合并文件分片 上下文实体
 */
@Data
public class MergeFileContext implements Serializable {
    private static final long serialVersionUID = -6399244960042497102L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件的唯一标识
     */
    private String identifier;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 文件分片的真实物理存储路径集合
     */
    private List<String> realPathList;

    /**
     * 文件合并后的真实物理存储路径
     */
    private String realPath;
}
