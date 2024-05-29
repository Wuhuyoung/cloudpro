package com.cloud.pro.server.modules.controller;

import com.cloud.pro.server.common.annotation.LoginIgnore;
import com.cloud.pro.server.common.utils.UserIdUtil;
import com.cloud.pro.core.response.Result;
import com.cloud.pro.core.utils.IdUtil;
import com.cloud.pro.server.modules.context.user.ChangePasswordContext;
import com.cloud.pro.server.modules.context.user.CheckAnswerContext;
import com.cloud.pro.server.modules.context.user.CheckUsernameContext;
import com.cloud.pro.server.modules.context.user.QueryUserSearchHistoryContext;
import com.cloud.pro.server.modules.context.user.ResetPasswordContext;
import com.cloud.pro.server.modules.context.user.UserLoginContext;
import com.cloud.pro.server.modules.context.user.UserRegisterContext;
import com.cloud.pro.server.modules.converter.UserConverter;
import com.cloud.pro.server.modules.po.user.ChangePasswordPO;
import com.cloud.pro.server.modules.po.user.CheckAnswerPO;
import com.cloud.pro.server.modules.po.user.CheckUsernamePO;
import com.cloud.pro.server.modules.po.user.ResetPasswordPO;
import com.cloud.pro.server.modules.po.user.UserLoginPO;
import com.cloud.pro.server.modules.service.UserSearchHistoryService;
import com.cloud.pro.server.modules.service.UserService;
import com.cloud.pro.server.modules.po.user.UserRegisterPO;
import com.cloud.pro.server.modules.vo.UserSearchHistoryVO;
import com.cloud.pro.server.modules.vo.UserVO;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user")
@Api(tags = "用户模块")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private UserConverter userConverter;

    @Resource
    private UserSearchHistoryService userSearchHistoryService;

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
    public Result<?> exit() {
        userService.exit(UserIdUtil.get());
        return Result.success();
    }

    /**
     * 忘记密码-校验用户名
     * @param checkUsernamePO
     * @return
     */
    @PostMapping("/username/check")
    @LoginIgnore
    public Result<String> checkUsername(@RequestBody @Validated CheckUsernamePO checkUsernamePO) {
        CheckUsernameContext checkUsernameContext = userConverter.checkUsernamePO2checkUsernameContext(checkUsernamePO);
        String question = userService.checkUsername(checkUsernameContext);
        return Result.data(question);
    }

    /**
     * 忘记密码-校验密保答案
     * @param checkAnswerPO
     * @return
     */
    @PostMapping("/answer/check")
    @LoginIgnore
    public Result<String> checkAnswer(@RequestBody @Validated CheckAnswerPO checkAnswerPO) {
        CheckAnswerContext checkAnswerContext = userConverter.checkAnswerPO2CheckAnswerContext(checkAnswerPO);
        String token = userService.checkAnswer(checkAnswerContext);
        return Result.data(token);
    }

    /**
     * 忘记密码-重置密码
     * @param resetPasswordPO
     * @return
     */
    @PostMapping("/password/reset")
    @LoginIgnore
    public Result<?> resetPassword(@RequestBody @Validated ResetPasswordPO resetPasswordPO) {
        ResetPasswordContext resetPasswordContext = userConverter.resetPasswordPO2ResetPasswordContext(resetPasswordPO);
        userService.resetPassword(resetPasswordContext);
        return Result.success();
    }

    /**
     * 修改密码
     * @param changePasswordPO
     * @return
     */
    @PostMapping("/password/change")
    public Result<?> changePassword(@RequestBody @Validated ChangePasswordPO changePasswordPO) {
        ChangePasswordContext changePasswordContext = userConverter.changePasswordPO2ChangePasswordContext(changePasswordPO);
        Long userId = UserIdUtil.get();
        changePasswordContext.setUserId(userId);
        userService.changePassword(changePasswordContext);
        return Result.success();
    }

    /**
     * 查询用户信息
     * @return
     */
    @GetMapping("/")
    public Result<UserVO> info() {
        UserVO userVO = userService.info(UserIdUtil.get());
        return Result.data(userVO);
    }

    /**
     * 获取用户最新的搜索历史记录，默认十条
     * @return
     */
    @GetMapping("/search/histories")
    public Result<List<UserSearchHistoryVO>> getUserSearchHistories() {
        QueryUserSearchHistoryContext context = new QueryUserSearchHistoryContext();
        context.setUserId(UserIdUtil.get());
        List<UserSearchHistoryVO> result = userSearchHistoryService.getUserSearchHistories(context);
        return Result.data(result);
    }
}
