package com.cloud.pro.server.modules.context.share;

import com.cloud.pro.server.modules.entity.Share;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建分享链接 上下文对象
 */
@Data
public class CreateShareUrlContext implements Serializable {
    private static final long serialVersionUID = 1826419697098626935L;

    /**
     * 分享的名称
     */
    private String shareName;

    /**
     * 分享的类型
     */
    private Integer shareType;

    /**
     * 分享的日期类型
     */
    private Integer shareDayType;

    /**
     * 分享的文件ID集合
     */
    private List<Long> shareFileIdList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 分享实体
     */
    private Share record;
}
