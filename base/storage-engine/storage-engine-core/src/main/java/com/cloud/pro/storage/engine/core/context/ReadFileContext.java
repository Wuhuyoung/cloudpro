package com.cloud.pro.storage.engine.core.context;

import lombok.Data;

import java.io.OutputStream;
import java.io.Serializable;

/**
 * 文件读取 上下文实体
 */
@Data
public class ReadFileContext implements Serializable {
    private static final long serialVersionUID = 3396517234398348369L;

    /**
     * 文件的真实存储路径
     */
    private String realPath;

    /**
     * 文件的输出流
     */
    private OutputStream outputStream;
}
