package com.cloud.pro.server.modules.context.file;

import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * 文件预览 上下文实体
 */
@Data
public class FilePreviewContext implements Serializable {
    private static final long serialVersionUID = 6373057493698530136L;
    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 请求响应对象
     */
    private HttpServletResponse response;

    /**
     * 当前登录用户ID
     */
    private Long userId;
}
