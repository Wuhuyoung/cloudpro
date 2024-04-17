package com.cloud.pro.server.modules.context.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 忘记密码-校验密保答案
 */
@Data
public class CheckAnswerContext implements Serializable {

    private static final long serialVersionUID = -3815777358696445064L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密保问题
     */
    private String question;

    /**
     * 密保答案
     */
    private String answer;
}
