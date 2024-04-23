package com.cloud.pro.server.modules.po.recycle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文件彻底删除 请求参数对象
 */
@Data
public class DeletePO implements Serializable {
    private static final long serialVersionUID = -8858551771948986509L;

    @ApiModelProperty(value = "要删除的文件ID集合，多个使用公用分隔符分割", required = true)
    @NotBlank(message = "请选择要删除的文件")
    private String fileIds;
}
