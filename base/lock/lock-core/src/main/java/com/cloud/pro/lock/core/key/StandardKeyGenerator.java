package com.cloud.pro.lock.core.key;

import com.cloud.pro.lock.core.LockContext;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 标准的key生成器
 */
@Component
public class StandardKeyGenerator extends AbstractKeyGenerator {

    /**
     * 标准key的生成方法
     * 格式：className:methodName:parameterType1:...:value1:value2:...
     *
     * @param lockContext
     * @param keyValueMap
     * @return
     */
    @Override
    protected String doGenerateKey(LockContext lockContext, Map<String, String> keyValueMap) {
        List<String> keyList = Lists.newArrayList();
        keyList.add(lockContext.getClassName());
        keyList.add(lockContext.getMethodName());

        Class<?>[] parameterTypes = lockContext.getParameterTypes();
        if (ArrayUtils.isNotEmpty(parameterTypes)) {
            Arrays.stream(parameterTypes).forEach(parameterType -> keyList.add(parameterType.toString()));
        } else {
            keyList.add(Void.class.toString());
        }

        Collection<String> values = keyValueMap.values();
        if (CollectionUtils.isNotEmpty(values)) {
            values.stream().forEach(value -> keyList.add(value));
        }
        // 合并的两种方式
//        return keyList.stream().collect(Collectors.joining(":"));
        return Joiner.on(":").join(keyList);
    }
}
