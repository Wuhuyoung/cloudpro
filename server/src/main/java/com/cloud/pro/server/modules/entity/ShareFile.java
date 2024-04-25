package com.cloud.pro.server.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户分享文件表
 * @TableName cloud_pro_share_file
 */
@TableName(value ="cloud_pro_share_file")
@Data
public class ShareFile implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分享id
     */
    private Long shareId;

    /**
     * 文件记录ID
     */
    private Long fileId;

    /**
     * 分享创建人
     */
    private Long createUser;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}