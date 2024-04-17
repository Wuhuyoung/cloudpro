package com.cloud.pro.server.modules.context.user;

import com.cloud.pro.server.modules.entity.User;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录上下文实体对象
 */
@Data
public class UserLoginContext implements Serializable {
    private static final long serialVersionUID = 5709041748374681491L;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;

    /**
     * 用户实体类
     */
    private User entity;

    /**
     * 用户登录后生成的token
     */
    private String accessToken;
}
