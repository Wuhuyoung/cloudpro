package com.cloud.pro.server.modules.context;

import lombok.Data;

import java.io.Serializable;

/**
 * 忘记密码-重置密码上下文信息
 */
@Data
public class ResetPasswordContext implements Serializable {

    private static final long serialVersionUID = 8034882390119751504L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 新密码
     */
    private String password;

    /**
     * 重置密码的token
     */
    private String token;
}
