package com.cloud.pro.server.modules.po.share;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 保存至我的文件夹 请求参数对象
 */
@Data
public class ShareSavePO implements Serializable {
    private static final long serialVersionUID = 7963503548951880030L;

    @ApiModelProperty(value = "要保存的文件ID集合，多个使用公用分隔符拼接", required = true)
    @NotBlank(message = "请选择要保存的文件")
    private String fileIds;

    @ApiModelProperty(value = "要保存到的文件夹ID", required = true)
    @NotBlank(message = "请选择要保存到的文件夹")
    private String targetParentId;
}
