package com.cloud.pro.server.modules.service.cache;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 自定义缓存key生成器
 */
@Component(value = "userIdKeyGenerator")
public class UserIdKeyGenerator implements KeyGenerator {

    private static final String USER_ID_PREFIX = "USER:ID:";

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder stringBuilder = new StringBuilder(USER_ID_PREFIX);
        if (params.length == 0) {
            return stringBuilder.toString();
        }
        for (Object param : params) {
            if (param instanceof Serializable) {
                stringBuilder.append(param);
                return stringBuilder.toString();
            }
        }
        stringBuilder.append(StringUtils.arrayToCommaDelimitedString(params));
        return stringBuilder.toString();
    }
}
