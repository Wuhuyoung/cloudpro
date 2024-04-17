package com.cloud.pro.server.modules.po.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 忘记密码-校验用户名 请求参数对象
 */
@Data
public class CheckUsernamePO implements Serializable {
    private static final long serialVersionUID = -6226288223976747588L;

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa50-9a-zA-Z]{4,16}$", message = "请输入4-16位只包含中文、数字和字母的用户名")
    private String username;
}
