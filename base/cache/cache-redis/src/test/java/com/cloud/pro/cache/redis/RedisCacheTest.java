package com.cloud.pro.cache.redis;

import cn.hutool.core.lang.Assert;
import com.cloud.pro.cache.core.constants.CacheConstants;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@SpringBootApplication
public class RedisCacheTest {

    @Resource
    private CacheManager cacheManager;

    @Resource
    private CacheAnnotationTester tester;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void caffeineCacheManagerTest() {
        Cache cache = cacheManager.getCache(CacheConstants.CACHE_NAME);
        Assert.notNull(cache);
        cache.put("name", "Mike");
        Assert.isTrue("Mike".equals(Objects.requireNonNull(cache.get("name")).get().toString()));
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

    @Test
    public void redisTemplateTest() {
        Assert.notNull(redisTemplate);
        redisTemplate.opsForValue().set("test", "testResult", 20, TimeUnit.SECONDS);
        String test = (String) redisTemplate.opsForValue().get("test");
        Assert.isTrue("testResult".equals(test));
    }
}
