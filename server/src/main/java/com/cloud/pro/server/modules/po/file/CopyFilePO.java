package com.cloud.pro.server.modules.po.file;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文件复制 请求参数对象
 */
@Data
public class CopyFilePO implements Serializable {
    private static final long serialVersionUID = -1656799224685239281L;

    @ApiModelProperty(value = "要复制的文件ID集合", required = true)
    @NotBlank(message = "请选择要复制的文件")
    private String fileIds;

    @ApiModelProperty(value = "要复制到的目标文件夹ID", required = true)
    @NotBlank(message = "请选择要复制到哪个文件夹下面")
    private String targetParentId;
}
