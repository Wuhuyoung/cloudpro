package com.cloud.pro.server.modules.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 物理文件信息表
 * @TableName cloud_pro_file
 */
@TableName(value ="cloud_pro_file")
@Data
public class File implements Serializable {
    /**
     * 文件id
     */
    @TableId
    private Long fileId;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件物理路径
     */
    private String realPath;

    /**
     * 文件实际大小
     */
    private String fileSize;

    /**
     * 文件大小展示字符
     */
    private String fileSizeDesc;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * 文件预览的响应头Content-Type的值
     */
    private String filePreviewContentType;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}