package com.cloud.pro.cache.caffeine.config;

import com.cloud.pro.cache.core.constants.CacheConstants;
import com.cloud.pro.core.constants.CommonConstants;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Caffeine cache配置类
 */
@Configuration
@Slf4j
@EnableCaching
@ComponentScan(value = CommonConstants.BASE_COMPONENT_SCAN_PATH + ".cache.caffeine.config")
public class CaffeineCacheConfig {

    @Resource
    private CaffeineCacheProperties properties;

    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(CacheConstants.CACHE_NAME);
        cacheManager.setAllowNullValues(properties.getAllowNullValue());
        Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
                .initialCapacity(properties.getInitCacheCapacity())
                .maximumSize(properties.getMaxCacheCapacity());
        cacheManager.setCaffeine(caffeineBuilder);
        log.info("the caffeine cache manager is loaded successfully!");
        return cacheManager;
    }
}
