package com.cloud.pro.server.modules.context;

import com.cloud.pro.server.modules.entity.User;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册上下文实体对象
 */
@Data
public class UserRegisterContext implements Serializable {
    private static final long serialVersionUID = -7685754005109133223L;

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
}
