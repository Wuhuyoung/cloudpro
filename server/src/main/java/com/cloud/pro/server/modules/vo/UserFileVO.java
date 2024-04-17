package com.cloud.pro.server.modules.vo;

import com.cloud.pro.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 用户查询文件列表响应实体
 */
@Data
public class UserFileVO implements Serializable {
    private static final long serialVersionUID = -668602235345525956L;
    /**
     * 文件ID
     */
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long fileId;

    /**
     * 父文件夹ID
     */
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 是否是文件夹（0 否 1 是）
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
     * 更新时间
     */
    private LocalDate updateTime;
}
