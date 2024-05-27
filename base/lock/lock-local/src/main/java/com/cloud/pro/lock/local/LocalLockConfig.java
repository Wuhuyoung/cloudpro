package com.cloud.pro.lock.local;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.locks.DefaultLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

/**
 * 本地锁配置类
 */
@Configuration
@Slf4j
public class LocalLockConfig {

    /**
     * 配置本地锁注册器
     * @return
     */
    @Bean
    public LockRegistry localLockRegistry() {
        // 底层模式实现就是ReentrantLock
        DefaultLockRegistry lockRegistry = new DefaultLockRegistry();
        log.info("the local lock is loaded successfully!");
        return lockRegistry;
    }
}
