package com.cloud.pro.server.modules.context.share;

import com.cloud.pro.server.modules.entity.Share;
import com.cloud.pro.server.modules.vo.ShareDetailVO;
import lombok.Data;

import java.io.Serializable;

/**
 * 查询分享详情 上下文实体
 */
@Data
public class QueryShareDetailContext implements Serializable {
    private static final long serialVersionUID = -7541826404050861299L;
    /**
     * 对应的分享ID
     */
    private Long shareId;

    /**
     * 分享实体
     */
    private Share record;

    /**
     * 分享详情的VO
     */
    private ShareDetailVO vo;
}
