package com.cloud.pro.server.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 文件分片信息表
 * @TableName cloud_pro_file_chunk
 */
@TableName(value ="cloud_pro_file_chunk")
@Data
public class FileChunk implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 分片真实的存储路径
     */
    private String realPath;

    /**
     * 分片编号
     */
    private Integer chunkNumber;

    /**
     * 过期时间
     */
    private LocalDateTime expirationTime;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}