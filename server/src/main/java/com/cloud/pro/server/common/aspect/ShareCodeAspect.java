package com.cloud.pro.server.common.aspect;

import com.cloud.pro.core.response.ResponseCode;
import com.cloud.pro.core.response.Result;
import com.cloud.pro.core.utils.JwtUtil;
import com.cloud.pro.server.common.annotation.LoginIgnore;
import com.cloud.pro.server.common.annotation.NeedShareCode;
import com.cloud.pro.server.common.utils.ShareIdUtil;
import com.cloud.pro.server.common.utils.UserIdUtil;
import com.cloud.pro.server.constants.ShareConstants;
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
 * 统一的分享码校验切面逻辑实现类
 */
@Component
@Aspect
@Slf4j
public class ShareCodeAspect {
    /**
     * 分享token校验参数名称
     */
    private static final String SHARE_CODE_PARAM_NAME = "shareToken";

    /**
     * 请求头分享token校验key
     */
    private static final String SHARE_CODE_REQUEST_HEADER_NAME = "Share-Token";

    /**
     * 切点表达式
     */
    private static final String POINT_CUT = "@annotation(com.cloud.pro.server.common.annotation.NeedShareCode)";

    /**
     * 切点模板方法
     */
    @Pointcut(value = POINT_CUT)
    public void shareCodeAuth() {

    }

    /**
     * 切点的环绕增强逻辑
     * 1、判断是否需要校验分享token信息
     * 2、校验token信息
     *  a.从请求头或参数中获取 token（JWT加密）
     *  b.解析 token，获得 shareId
     *  c.将 shareId 存入线程上下文，供下游使用
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("shareCodeAuth()")
    public Object shareCodeAuthAround(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String requestURI = request.getRequestURI();
        if (!checkAndSaveShareId(request)) {
            log.warn("成功拦截到请求，URI为{}，检测到用户的分享码失效，即将跳转到分享码校验页面", requestURI);
            return Result.fail(ResponseCode.ACCESS_DENIED);
        }
        return joinPoint.proceed();
    }

    /**
     * 校验token并保存shareId
     * @param request
     * @return
     */
    private boolean checkAndSaveShareId(HttpServletRequest request) {
        // 1.从请求头或请求参数中获取token
        String shareToken = request.getHeader(SHARE_CODE_REQUEST_HEADER_NAME);
        if (StringUtils.isBlank(shareToken)) {
            shareToken = request.getParameter(SHARE_CODE_PARAM_NAME);
        }
        // 没有token
        if (StringUtils.isBlank(shareToken)) {
            return false;
        }
        // 2.解析token，获得shareId
        Object shareId = JwtUtil.analyzeToken(shareToken, ShareConstants.SHARE_ID);
        // token非法或者过期
        if (Objects.isNull(shareId)) {
            return false;
        }
        saveShareId(shareId);
        return true;
    }

    /**
     * 保存shareId到线程上下文中
     * @param shareId
     */
    private void saveShareId(Object shareId) {
        ShareIdUtil.set(Long.valueOf(String.valueOf(shareId)));
    }
}
