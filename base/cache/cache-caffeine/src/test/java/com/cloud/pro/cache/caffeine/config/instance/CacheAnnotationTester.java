package com.cloud.pro.cache.caffeine.config.instance;

import com.cloud.pro.cache.core.constants.CacheConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CacheAnnotationTester {

    /**
     * 测试 缓存注解是否生效
     * sync: 如果为true，当多线程并发查询时只会有一条线程去数据库中查询，其他都只能阻塞等待
     * @param name
     * @return
     */
    @Cacheable(cacheNames = CacheConstants.CACHE_NAME, key = "#name", sync = true)
    public String testCache(String name) {
        log.info("模拟从数据库中查询");
        return "hello, " + name;
    }
}
