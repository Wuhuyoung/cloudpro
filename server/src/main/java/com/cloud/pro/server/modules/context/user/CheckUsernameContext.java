package com.cloud.pro.server.modules.context.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 忘记密码-校验用户名称
 */
@Data
public class CheckUsernameContext implements Serializable {

    private static final long serialVersionUID = -4918883020521005891L;
    /**
     * 用户名
     */
    private String username;
}
