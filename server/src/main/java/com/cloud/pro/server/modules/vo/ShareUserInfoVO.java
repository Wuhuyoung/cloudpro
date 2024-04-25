package com.cloud.pro.server.modules.vo;

import com.cloud.pro.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

/**
 * 分享者的信息 响应实体
 */
@Data
public class ShareUserInfoVO implements Serializable {
    private static final long serialVersionUID = 2590563097966022244L;

    /**
     * 分享者的ID
     */
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long userId;

    /**
     * 分享者的名称
     */
    private String username;
}
