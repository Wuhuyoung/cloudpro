package com.cloud.pro.server.modules.context.share;

import com.cloud.pro.server.modules.entity.Share;
import com.cloud.pro.server.modules.vo.ShareSimpleDetailVO;
import lombok.Data;

import java.io.Serializable;

/**
 * 查询分享的简单详情 上下文实体
 */
@Data
public class QueryShareSimpleDetailContext implements Serializable {
    private static final long serialVersionUID = -2722620950600631260L;

    /**
     * 分享ID
     */
    private Long shareId;

    /**
     * 分享实体信息
     */
    private Share record;

    /**
     * 简单分享详情的VO对象
     */
    private ShareSimpleDetailVO vo;
}
