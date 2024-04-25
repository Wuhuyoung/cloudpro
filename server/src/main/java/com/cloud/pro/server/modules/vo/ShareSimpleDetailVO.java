package com.cloud.pro.server.modules.vo;

import com.cloud.pro.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

/**
 * 分享的简单详情 响应实体
 */
@Data
public class ShareSimpleDetailVO implements Serializable {
    private static final long serialVersionUID = 5485368649665411343L;
    /**
     * 分享ID
     */
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    /**
     * 分享名称
     */
    private String shareName;

    /**
     * 分享者信息
     */
    private ShareUserInfoVO shareUserInfoVO;
}
