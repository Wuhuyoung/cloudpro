package com.cloud.pro.server.modules.context.file;

import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * 文件下载 上下文实体
 */
@Data
public class FileDownloadContext implements Serializable {
    private static final long serialVersionUID = -3437420199033178476L;

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
