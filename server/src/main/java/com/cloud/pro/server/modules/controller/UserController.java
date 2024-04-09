package com.cloud.pro.server.modules.controller;

import com.cloud.pro.server.common.annotation.LoginIgnore;
import com.cloud.pro.server.common.utils.UserIdUtil;
import com.cloud.pro.core.response.Result;
import com.cloud.pro.core.utils.IdUtil;
import com.cloud.pro.server.modules.context.UserLoginContext;
import com.cloud.pro.server.modules.context.UserRegisterContext;
import com.cloud.pro.server.modules.converter.UserConverter;
import com.cloud.pro.server.modules.po.UserLoginPO;
import com.cloud.pro.server.modules.service.UserService;
import com.cloud.pro.server.modules.po.UserRegisterPO;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
@Api(tags = "用户模块")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private UserConverter userConverter;

    /**
     * 用户注册（幂等）
     * @param userRegisterPO
     * @return
     */
    @PostMapping("/register")
    @LoginIgnore
    public Result<String> register(@RequestBody @Validated UserRegisterPO userRegisterPO) {
        UserRegisterContext userRegisterContext = userConverter.registerPO2RegisterContext(userRegisterPO);
        Long userId = userService.register(userRegisterContext);
        return Result.data(IdUtil.encrypt(userId));
    }

    /**
     * 用户登录
     * @param userLoginPO
     * @return 有时效性的accessToken
     */
    @PostMapping("/login")
    @LoginIgnore
    public Result<String> login(@RequestBody @Validated UserLoginPO userLoginPO) {
        UserLoginContext userLoginContext = userConverter.loginPO2LoginContext(userLoginPO);
        String accessToken = userService.login(userLoginContext);
        return Result.data(accessToken);
    }

    /**
     * 用户登出
     * @return
     */
    @PostMapping("/exit")
    public Result exit() {
        userService.exit(UserIdUtil.get());
        return Result.success();
    }
}
