package com.cloud.pro.server.modules.vo;

import com.cloud.pro.web.serializer.IdEncryptSerializer;
import com.cloud.pro.web.serializer.LocalDateTime2StringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分享链接列表 响应实体
 */
@Data
public class ShareUrlListVO implements Serializable {
    private static final long serialVersionUID = -2620008767893015598L;

    /**
     * 分享的ID
     */
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    /**
     * 分享的名称
     */
    private String shareName;

    /**
     * 分享 Url
     */
    private String shareUrl;

    /**
     * 分享的分享码
     */
    private String shareCode;

    /**
     * 分享状态
     */
    private Integer shareStatus;

    /**
     * 分享类型
     */
    private Integer shareType;

    /**
     * 分享的过期类型
     */
    private Integer shareDayType;

    /**
     * 分享的过期时间
     */
    @JsonSerialize(using = LocalDateTime2StringSerializer.class)
    private LocalDateTime shareEndTime;

    /**
     * 分享的创建时间
     */
    @JsonSerialize(using = LocalDateTime2StringSerializer.class)
    private LocalDateTime createTime;
}
