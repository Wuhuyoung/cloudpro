package com.cloud.pro.server.modules.po;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 忘记密码-校验密保答案 请求参数对象
 */
@Data
public class CheckAnswerPO implements Serializable {
    private static final long serialVersionUID = -4385083096682170530L;

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa50-9a-zA-Z]{4,16}$", message = "请输入4-16位只包含中文、数字和字母的用户名")
    private String username;

    @NotBlank(message = "密保问题不能为空")
    @Length(max = 100, message = "密保问题不能超过100个字符")
    private String question;

    @NotBlank(message = "密保答案不能为空")
    @Length(max = 100, message = "密保答案不能超过100个字符")
    private String answer;
}
