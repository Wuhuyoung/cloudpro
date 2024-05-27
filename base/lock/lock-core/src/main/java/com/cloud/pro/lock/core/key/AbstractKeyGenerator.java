package com.cloud.pro.lock.core.key;

import com.cloud.pro.core.utils.SpElUtil;
import com.cloud.pro.lock.core.LockContext;
import com.cloud.pro.lock.core.annotation.Lock;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * 锁的key生成器 公用父类
 */
public abstract class AbstractKeyGenerator implements KeyGenerator {

    /**
     * 生成锁的Key
     * @param lockContext
     * @return
     */
    @Override
    public String generateKey(LockContext lockContext) {
        Lock annotation = lockContext.getAnnotation();
        // key可以是字符串，也可以是SpringEL表达式，所以我们解析成一个Map，key是原本的key，value是解析后的key
        String[] keys = annotation.keys();
        Map<String, String> keyValueMap = Maps.newHashMap();
        if (ArrayUtils.isNotEmpty(keys)) {
            Arrays.stream(keys).forEach(key -> {
                keyValueMap.put(key, SpElUtil.getStringValue(key, lockContext.getClassName(), lockContext.getMethodName(),
                        lockContext.getClassType(), lockContext.getMethod(), lockContext.getArgs(),
                        lockContext.getParameterTypes(), lockContext.getTarget()));
            });
        }
        return doGenerateKey(lockContext, keyValueMap);
    }

    protected abstract String doGenerateKey(LockContext lockContext, Map<String, String> keyValueMap);
}
