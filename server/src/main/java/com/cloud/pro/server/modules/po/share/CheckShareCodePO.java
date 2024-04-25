package com.cloud.pro.server.modules.po.share;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 校验分享码 请求参数对象
 */
@Data
public class CheckShareCodePO implements Serializable {
    private static final long serialVersionUID = -5262014283768363217L;

    @ApiModelProperty(value = "分享的ID", required = true)
    @NotBlank(message = "分享的ID不能为空")
    private String shareId;

    @ApiModelProperty(value = "分享的分享码", required = true)
    @NotBlank(message = "分享的分享码不能为空")
    private String shareCode;
}
