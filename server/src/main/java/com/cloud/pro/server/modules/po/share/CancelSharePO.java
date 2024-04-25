package com.cloud.pro.server.modules.po.share;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 取消分享 请求参数对象
 */
@Data
public class CancelSharePO implements Serializable {
    private static final long serialVersionUID = 8765830544566565722L;

    @ApiModelProperty(value = "要取消的分享ID的集合，多个使用公用分隔符拼接", required = true)
    @NotBlank(message = "请选择要取消的分享")
    private String shareIds;
}
