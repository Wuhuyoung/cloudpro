package com.cloud.pro.server.modules.vo;

import com.cloud.pro.web.serializer.IdEncryptSerializer;
import com.cloud.pro.web.serializer.LocalDateTime2StringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分享详情 响应实体
 */
@Data
public class ShareDetailVO implements Serializable {
    private static final long serialVersionUID = -7334793467901522119L;

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
     * 创建时间
     */
    @JsonSerialize(using = LocalDateTime2StringSerializer.class)
    private LocalDateTime createTime;

    /**
     * 分享的过期类型
     */
    private Integer shareDay;

    /**
     * 截至时间
     */
    @JsonSerialize(using = LocalDateTime2StringSerializer.class)
    private LocalDateTime shareEndTime;

    /**
     * 分享的文件列表
     */
    private List<UserFileVO> userFileVOList;

    /**
     * 分享者的信息
     */
    private ShareUserInfoVO shareUserInfoVO;
}
