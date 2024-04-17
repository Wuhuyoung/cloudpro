package com.cloud.pro.server.modules.po.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 忘记密码-重置密码 请求参数对象
 */
@Data
public class ResetPasswordPO implements Serializable {
    private static final long serialVersionUID = 2434458461633886650L;

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa50-9a-zA-Z]{4,16}$", message = "请输入4-16位只包含中文、数字和字母的用户名")
    private String username;

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 16, message = "请输入6-16位的密码")
    private String password;

    @NotBlank(message = "token不能为空")
    private String token;
}
