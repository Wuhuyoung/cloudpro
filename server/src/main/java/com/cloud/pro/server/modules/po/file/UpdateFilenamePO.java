package com.cloud.pro.server.modules.po.file;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文件重命名 请求参数对象
 */
@Data
public class UpdateFilenamePO implements Serializable {

    private static final long serialVersionUID = -975657588358143551L;

    @ApiModelProperty(value = "修改的文件ID", required = true)
    @NotBlank(message = "修改的文件ID不能为空")
    private String fileId;

    @ApiModelProperty(value = "新的文件名称", required = true)
    @NotBlank(message = "新的文件名称不能为空")
    private String newFilename;
}
