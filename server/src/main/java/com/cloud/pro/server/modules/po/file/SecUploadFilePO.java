package com.cloud.pro.server.modules.po.file;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文件秒传 请求参数对象
 */
@Data
public class SecUploadFilePO implements Serializable {
    private static final long serialVersionUID = 1595604012533530615L;

    @ApiModelProperty(value = "文件夹ID", required = true)
    @NotBlank(message = "文件夹ID不能为空")
    private String parentId;

    @ApiModelProperty(value = "文件名称", required = true)
    @NotBlank(message = "文件名称不能为空")
    private String filename;

    @ApiModelProperty(value = "文件的唯一标识", required = true)
    @NotBlank(message = "文件的唯一标识不能为空")
    private String identifier;
}
