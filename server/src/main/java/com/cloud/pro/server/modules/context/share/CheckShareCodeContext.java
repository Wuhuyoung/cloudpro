package com.cloud.pro.server.modules.context.share;

import lombok.Data;

import java.io.Serializable;

/**
 * 校验分享码 上下文实体
 */
@Data
public class CheckShareCodeContext implements Serializable {
    private static final long serialVersionUID = 9083995361390922663L;

    /**
     * 分享的ID
     */
    private Long shareId;

    /**
     * 分享的分享码
     */
    private String shareCode;
}
