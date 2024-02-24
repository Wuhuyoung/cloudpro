package com.cloud.pro.cache.caffeine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Caffeine cache自定义配置属性类
 */
@Data
@Component
@ConfigurationProperties(prefix = "cloud.pro.caffeine")
public class CaffeineCacheProperties {

    /**
     * 缓存初始容量
     */
    private Integer initCacheCapacity = 256;

    /**
     * 缓存最大容量，超过之后按照LRU策略删除
     */
    private Long maxCacheCapacity = 10000L;

    /**
     * 是否允许null作为缓存的value
     */
    private Boolean allowNullValue = Boolean.TRUE;
}
