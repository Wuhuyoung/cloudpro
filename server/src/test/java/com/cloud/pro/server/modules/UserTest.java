package com.cloud.pro.server.modules;

import cn.hutool.core.lang.Assert;
import com.cloud.pro.server.constants.UserConstants;
import com.cloud.pro.core.exception.BusinessException;
import com.cloud.pro.core.utils.JwtUtil;
import com.cloud.pro.server.modules.context.user.ChangePasswordContext;
import com.cloud.pro.server.modules.context.user.CheckAnswerContext;
import com.cloud.pro.server.modules.context.user.CheckUsernameContext;
import com.cloud.pro.server.modules.context.user.ResetPasswordContext;
import com.cloud.pro.server.modules.context.user.UserLoginContext;
import com.cloud.pro.server.modules.service.UserService;
import com.cloud.pro.server.modules.context.user.UserRegisterContext;
import com.cloud.pro.server.modules.vo.UserVO;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 用户服务测试
 */
@SpringBootTest
@Transactional // 不管是否有异常都会回滚，防止测试用例污染数据库
public class UserTest {
    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 测试成功注册用户信息
     */
    @Test
    public void testRegister() {
        UserRegisterContext context = createUserRegisterContext();

        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);
    }

    /**
     * 测试重复用户名称注册幂等
     */
    @Test
    public void testRegisterDuplicateUsername() {
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            UserRegisterContext context = createUserRegisterContext();
            Long userId = userService.register(context);
            Assert.isTrue(userId > 0L);
            userService.register(context);
        });
        Assert.isTrue("用户名已存在".equals(exception.getMessage()));
    }

    /**
     * 测试用户登录成功
     */
    @Test
    public void testLoginSuccess() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);

        UserLoginContext loginContext = getUserLoginContext();

        String accessToken = userService.login(loginContext);
        Assert.notBlank(accessToken);
    }

    @Test
    public void testLoginWrongUsername() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);

        UserLoginContext loginContext = getUserLoginContext();

        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            loginContext.setUsername(loginContext.getUsername() + "_change");
            userService.login(loginContext);
        });
        Assert.isTrue("用户名不存在".equals(exception.getMessage()));
    }

    @Test
    public void testLoginWrongPassword() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);

        UserLoginContext loginContext = getUserLoginContext();

        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            loginContext.setPassword(loginContext.getPassword() + "_change");
            userService.login(loginContext);
        });
        Assert.isTrue("用户名与密码不匹配".equals(exception.getMessage()));
    }

    @Test
    public void testExitSuccess() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);

        UserLoginContext loginContext = getUserLoginContext();

        String accessToken = userService.login(loginContext);
        Assert.notBlank(accessToken);

        // 测试成功退出登录
        Long id = (Long) JwtUtil.analyzeToken(accessToken, UserConstants.LOGIN_USER_ID);
        userService.exit(id);
        Object exitUserId = redisTemplate.opsForValue().get(UserConstants.USER_LOGIN_TOKEN_PREFIX + id);
        Assertions.assertNull(exitUserId);
    }

    @Test
    public void testCheckAnswer() {
        CheckUsernameContext checkUsernameContext = new CheckUsernameContext();
        checkUsernameContext.setUsername("1231");
        String question = userService.checkUsername(checkUsernameContext);
        Assertions.assertTrue(StringUtils.isNotBlank(question));

        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();
        checkAnswerContext.setUsername("1231");
        checkAnswerContext.setQuestion(question);
        checkAnswerContext.setAnswer("小猫");
        String token = userService.checkAnswer(checkAnswerContext);
        Assertions.assertTrue(StringUtils.isNotBlank(token));
    }

    @Test
    public void testCheckAnswerFail() {
        CheckUsernameContext checkUsernameContext = new CheckUsernameContext();
        checkUsernameContext.setUsername("1231");
        String question = userService.checkUsername(checkUsernameContext);
        Assertions.assertTrue(StringUtils.isNotBlank(question));
        System.out.println("question=" + question);

        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();
        checkAnswerContext.setUsername("1231");
        checkAnswerContext.setQuestion(question);
        checkAnswerContext.setAnswer("小狗");

        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            String token = userService.checkAnswer(checkAnswerContext);
        });
        Assert.isTrue("密保答案错误".equals(exception.getMessage()));
    }

    @Test
    public void testResetPassword() {
        CheckUsernameContext checkUsernameContext = new CheckUsernameContext();
        checkUsernameContext.setUsername("1231");
        String question = userService.checkUsername(checkUsernameContext);
        Assertions.assertTrue(StringUtils.isNotBlank(question));

        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();
        checkAnswerContext.setUsername("1231");
        checkAnswerContext.setQuestion(question);
        checkAnswerContext.setAnswer("小猫");
        String token = userService.checkAnswer(checkAnswerContext);
        Assertions.assertTrue(StringUtils.isNotBlank(token));

        ResetPasswordContext resetPasswordContext = new ResetPasswordContext();
        resetPasswordContext.setUsername("1231");
        resetPasswordContext.setPassword("123123");
        resetPasswordContext.setToken(token);
        userService.resetPassword(resetPasswordContext);
    }

    @Test
    public void testChangePassword() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);

        UserLoginContext loginContext = getUserLoginContext();

        String accessToken = userService.login(loginContext);

        ChangePasswordContext changePasswordContext = new ChangePasswordContext();
        changePasswordContext.setUserId(loginContext.getEntity().getUserId());
        changePasswordContext.setOldPassword(context.getPassword());
        changePasswordContext.setNewPassword("1234567");

        userService.changePassword(changePasswordContext);
    }

    @Test
    public void testChangePasswordFail() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);

        UserLoginContext loginContext = getUserLoginContext();

        String accessToken = userService.login(loginContext);

        ChangePasswordContext changePasswordContext = new ChangePasswordContext();
        changePasswordContext.setUserId(loginContext.getEntity().getUserId());
        changePasswordContext.setOldPassword(context.getPassword() + "_change");
        changePasswordContext.setNewPassword("1234567");

        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            userService.changePassword(changePasswordContext);
        });
        Assert.isTrue("原密码不正确".equals(exception.getMessage()));
    }

    @Test
    public void testGetUserInfo() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);

        UserLoginContext loginContext = getUserLoginContext();

        String accessToken = userService.login(loginContext);

        UserVO userVO = userService.info(userId);
        Assertions.assertNotNull(userVO);
    }

    /**************************************private**************************************/

    private final static String username = "测试用户1";
    private final static String password = "123456";
    /**
     * 创建用户上下文测试实体
     * @return
     */
    @NotNull
    private UserRegisterContext createUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername(username);
        context.setPassword(password);
        return context;
    }

    /**
     * 创建用户登录上下文测试实体
     * @return
     */
    @NotNull
    private UserLoginContext getUserLoginContext() {
        UserLoginContext loginContext = new UserLoginContext();
        loginContext.setUsername(username);
        loginContext.setPassword(password);
        return loginContext;
    }
}
