package com.cloud.pro.server.modules.vo;

import com.cloud.pro.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建分享链接响应实体
 */
@Data
public class ShareUrlVO implements Serializable {
    private static final long serialVersionUID = -7141456446162492257L;

    /**
     * 分享链接的ID
     */
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    /**
     * 分享链接的名称
     */
    private String shareName;

    /**
     * 分享链接 Url
     */
    private String shareUrl;

    /**
     * 分享链接的分享码
     */
    private String shareCode;

    /**
     * 分享链接的状态
     */
    private Integer shareStatus;
}
