package com.cloud.pro.server.modules.context.file;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryRealFileListContext implements Serializable {
    private static final long serialVersionUID = -5627125152443327474L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 文件的唯一标识
     */
    private String identifier;
}
