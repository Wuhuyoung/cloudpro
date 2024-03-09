package com.cloud.pro.server.constants;

/**
 * 用户模块常量类
 */
public interface UserConstants {

    /**
     * 登录用户的用户Id的key
     */
    String LOGIN_USER_ID = "LOGIN_USER_ID";

    /**
     * 一天的毫秒值
     */
    Long ONE_DAY_MS = 24 * 60 * 60 * 1000L;

    /**
     * 一周的毫秒值
     */
    Long ONE_WEEK_MS = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 用户登录accessToken缓存前缀
     */
    String USER_LOGIN_TOKEN_PREFIX = "user:login:token:";
}
