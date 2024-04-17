package com.cloud.pro.server.modules.po.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 用户修改密码 请求参数对象
 */
@Data
public class ChangePasswordPO implements Serializable {
    private static final long serialVersionUID = -324110169723112674L;

    @ApiModelProperty(value = "旧密码", required = true)
    @NotBlank(message = "旧密码不能为空")
    @Length(min = 6, max = 16, message = "请输入6-16位的密码")
    private String oldPassword;

    @ApiModelProperty(value = "新密码", required = true)
    @NotBlank(message = "新密码不能为空")
    @Length(min = 6, max = 16, message = "请输入6-16位的新密码")
    private String newPassword;
}
