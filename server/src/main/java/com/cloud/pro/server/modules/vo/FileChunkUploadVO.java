package com.cloud.pro.server.modules.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件分片上传 响应实体
 */
@Data
public class FileChunkUploadVO implements Serializable {
    private static final long serialVersionUID = 86415812708207481L;

    @ApiModelProperty(value = "是否需要合并文件 0 不需要 1 需要")
    private Integer mergeFlag;
}
