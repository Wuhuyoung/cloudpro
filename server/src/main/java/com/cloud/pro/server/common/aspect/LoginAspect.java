package com.cloud.pro.server.common.aspect;

import com.cloud.pro.core.response.ResponseCode;
import com.cloud.pro.core.response.Result;
import com.cloud.pro.core.utils.JwtUtil;
import com.cloud.pro.server.common.annotation.LoginIgnore;
import com.cloud.pro.server.common.utils.UserIdUtil;
import com.cloud.pro.server.constants.UserConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 统一的登录拦截校验切面逻辑实现类
 */
@Component
@Aspect
@Slf4j
public class LoginAspect {
    /**
     * 登录认证参数名称
     */
    private static final String LOGIN_AUTH_PARAM_NAME = "authorization";

    /**
     * 请求头登录认证key
     */
    private static final String LOGIN_AUTH_REQUEST_HEADER_NAME = "Authorization";

    /**
     * 切点表达式
     */
    private static final String POINT_CUT = "execution(* com.cloud.pro.server.modules.controller..*(..))";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 切点模板方法
     */
    @Pointcut(value = POINT_CUT)
    public void loginAuth() {

    }

    /**
     * 切点的环绕增强逻辑
     * 1、判断是否需要校验登录信息（使用了@LoginIgnore注解的无需校验）
     * 2、校验登录信息
     *  a.从请求头或参数中获取 token（JWT加密）
     *  b.从缓存中获取 token，进行比对
     *  c.解析 token，获得 userId
     *  d.将 userId 存入线程上下文，供下游使用
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("loginAuth()")
    public Object loginAuthAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (checkNeedCheckLoginInfo(joinPoint)) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String requestURI = request.getRequestURI();
            if (!checkAndSaveUserId(request)) {
                log.warn("成功拦截到请求，URI为{}，检测到用户未登录，即将跳转到登录页面", requestURI);
                return Result.fail(ResponseCode.NEED_LOGIN);
            }
        }
        return joinPoint.proceed();
    }

    /**
     * 校验token并保存用户id
     * @param request
     * @return
     */
    private boolean checkAndSaveUserId(HttpServletRequest request) {
        // 1.从请求头或请求参数中获取token
        String accessToken = request.getHeader(LOGIN_AUTH_REQUEST_HEADER_NAME);
        if (StringUtils.isBlank(accessToken)) {
            accessToken = request.getParameter(LOGIN_AUTH_PARAM_NAME);
        }
        // 没有token
        if (StringUtils.isBlank(accessToken)) {
            return false;
        }
        // 3.解析token，获得userId
        Object userId = JwtUtil.analyzeToken(accessToken, UserConstants.LOGIN_USER_ID);
        // 非法token
        if (Objects.isNull(userId)) {
            return false;
        }
        // 2.从缓存中获取token，进行比对
        Object redisAccessToken = redisTemplate.opsForValue().get(UserConstants.USER_LOGIN_TOKEN_PREFIX + userId);
        // 登录token非法或者过期
        if (Objects.isNull(redisAccessToken)) {
            return false;
        }
        if (Objects.equals(accessToken, redisAccessToken)) {
            saveUserId(userId);
            return true;
        }
        return false;
    }

    /**
     * 保存userId到线程上下文中
     * @param userId
     */
    private void saveUserId(Object userId) {
        UserIdUtil.set(Long.valueOf(String.valueOf(userId)));
    }

    /**
     * 校验方法是否需要 校验登录信息
     * @param joinPoint
     * @return
     */
    private boolean checkNeedCheckLoginInfo(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        return !method.isAnnotationPresent(LoginIgnore.class);
    }
}
