package com.cloud.pro.cache.caffeine.config;

import cn.hutool.core.lang.Assert;
import com.cloud.pro.cache.caffeine.config.instance.CacheAnnotationTester;
import com.cloud.pro.cache.core.constants.CacheConstants;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;


import javax.annotation.Resource;
import java.util.Objects;

@SpringBootTest
@SpringBootApplication
public class CaffeineCacheTest {
    @Resource
    private CacheManager cacheManager;

    @Resource
    private CacheAnnotationTester tester;

    @Test
    public void caffeineCacheManagerTest() {
        Cache cache = cacheManager.getCache(CacheConstants.CACHE_NAME);
        Assert.notNull(cache);
        cache.put("name", "Jack");
        Assert.isTrue("Jack".equals(Objects.requireNonNull(cache.get("name")).get().toString()));
    }

    /**
     * 测试注解
     */
    @Test
    public void caffeineCacheAnnotationTest() {
        for (int i = 0; i < 3; i++) {
            String mike = tester.testCache("Mike");
        }
    }
}
