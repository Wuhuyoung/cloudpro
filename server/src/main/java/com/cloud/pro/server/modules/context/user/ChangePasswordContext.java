package com.cloud.pro.server.modules.context.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户修改密码上下文对象
 */
@Data
public class ChangePasswordContext implements Serializable {

    private static final long serialVersionUID = -6831717636425644953L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;
}
