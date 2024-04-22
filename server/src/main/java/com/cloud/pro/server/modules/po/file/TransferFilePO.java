package com.cloud.pro.server.modules.po.file;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文件转移 请求参数对象
 */
@Data
public class TransferFilePO implements Serializable {
    private static final long serialVersionUID = 2222768421006189571L;

    @ApiModelProperty(value = "要转移的文件ID集合", required = true)
    @NotBlank(message = "请选择要转移的文件")
    private String fileIds;

    @ApiModelProperty(value = "要转移到的目标文件夹ID", required = true)
    @NotBlank(message = "请选择要转移到哪个文件夹下面")
    private String targetParentId;
}
