package com.cloud.pro.server.modules.po.file;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 批量删除文件 请求参数对象
 */
@Data
public class DeleteFilePO implements Serializable {
    private static final long serialVersionUID = -6529907947309920933L;

    @ApiModelProperty(value = "要删除的文件ID，多个使用公用的分隔符分割", required = true)
    @NotBlank(message = "请选择要删除的文件")
    private String fileIds;
}
