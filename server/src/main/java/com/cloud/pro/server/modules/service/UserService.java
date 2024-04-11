package com.cloud.pro.server.modules.service;

import com.cloud.pro.server.modules.context.ChangePasswordContext;
import com.cloud.pro.server.modules.context.CheckAnswerContext;
import com.cloud.pro.server.modules.context.CheckUsernameContext;
import com.cloud.pro.server.modules.context.ResetPasswordContext;
import com.cloud.pro.server.modules.context.UserLoginContext;
import com.cloud.pro.server.modules.context.UserRegisterContext;
import com.cloud.pro.server.modules.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.pro.server.modules.vo.UserVO;

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

    /**
     * 用户忘记密码 - 校验用户名
     * @param checkUsernameContext
     * @return
     */
    String checkUsername(CheckUsernameContext checkUsernameContext);

    /**
     * 用户忘记密码 - 校验密保答案
     * @param checkAnswerContext
     * @return
     */
    String checkAnswer(CheckAnswerContext checkAnswerContext);

    /**
     * 用户忘记密码 - 重置密码
     * @param resetPasswordContext
     */
    void resetPassword(ResetPasswordContext resetPasswordContext);

    /**
     * 修改密码
     * @param changePasswordContext
     */
    void changePassword(ChangePasswordContext changePasswordContext);

    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    UserVO info(Long userId);
}
