package com.cloud.pro.server.modules.po.recycle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 删除的文件还原 请求参数对象
 */
@Data
public class RestorePO implements Serializable {
    private static final long serialVersionUID = -6778044657824454478L;

    @ApiModelProperty(value = "要还原的文件ID集合，多个使用公用分隔符分割", required = true)
    @NotBlank(message = "请选择要还原的文件")
    private String fileIds;
}
