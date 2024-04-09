package com.cloud.pro.server.modules;

import cn.hutool.core.lang.Assert;
import com.cloud.pro.server.constants.UserConstants;
import com.cloud.pro.core.exception.BusinessException;
import com.cloud.pro.core.utils.JwtUtil;
import com.cloud.pro.server.modules.context.UserLoginContext;
import com.cloud.pro.server.modules.service.UserService;
import com.cloud.pro.server.modules.context.UserRegisterContext;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
