package com.cloud.pro.server.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 用户文件信息表
 * @TableName cloud_pro_user_file
 */
@TableName(value ="cloud_pro_user_file")
@Data
public class UserFile implements Serializable {
    /**
     * 文件ID
     */
    @TableId
    private Long fileId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 上级文件夹ID,顶级文件夹为0
     */
    private Long parentId;

    /**
     * 真实文件id
     */
    private Long realFileId;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 是否是文件夹 （0 否 1 是）
     */
    private Integer folderFlag;

    /**
     * 文件大小描述
     */
    private String fileSizeDesc;

    /**
     * 文件类型（1 普通文件 2 压缩文件 3 excel 4 word 5 pdf 6 txt 7 图片 8 音频 9 视频 10 ppt 11 源码文件 12 csv）
     */
    private Integer fileType;

    /**
     * 删除标识（0 否 1 是）
     */
    private Integer delFlag;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private Long updateUser;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}