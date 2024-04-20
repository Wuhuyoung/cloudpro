package com.cloud.pro.server.modules.po.file;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 查询用户已上传的文件分片列表 请求参数对象
 */
@Data
public class QueryUploadedChunksPO implements Serializable {
    private static final long serialVersionUID = -8597945078105820882L;

    @ApiModelProperty(value = "文件的唯一标识", required = true)
    @NotBlank(message = "文件的唯一标识不能为空")
    private String identifier;
}
