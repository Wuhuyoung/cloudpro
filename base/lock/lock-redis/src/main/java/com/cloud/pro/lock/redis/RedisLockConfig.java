package com.cloud.pro.lock.redis;

import com.cloud.pro.lock.core.LockConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

/**
 * 基于Redis实现分布式锁
 */
@Configuration
@Slf4j
public class RedisLockConfig {

    @Bean
    public LockRegistry RedisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        RedisLockRegistry redisLockRegistry = new RedisLockRegistry(redisConnectionFactory, LockConstants.CLOUD_PRO_LOCK);
        log.info("redis lock is loaded successfully!");
        return redisLockRegistry;
    }
}
