package com.cloud.pro.server.modules.service;

import com.cloud.pro.server.modules.context.UserLoginContext;
import com.cloud.pro.server.modules.context.UserRegisterContext;
import com.cloud.pro.server.modules.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author han
* @description 针对表【cloud_pro_user(用户信息表)】的数据库操作Service
* @createDate 2024-02-23 18:28:45
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userRegisterContext
     * @return
     */
    Long register(UserRegisterContext userRegisterContext);

    /**
     * 用户登录
     * @param userLoginContext
     * @return
     */
    String login(UserLoginContext userLoginContext);

    /**
     * 退出登录
     * @param userId
     */
    void exit(Long userId);
}
