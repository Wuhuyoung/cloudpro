package com.cloud.pro.lock.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.integration.zookeeper.config.CuratorFrameworkFactoryBean;
import org.springframework.integration.zookeeper.lock.ZookeeperLockRegistry;

import javax.annotation.Resource;

/**
 * zk分布式锁
 */
@Configuration
@Slf4j
public class ZookeeperLockConfig {

    @Resource
    private ZookeeperLockProperties properties;

    /**
     * 配置zk的客户端
     * @return
     */
    @Bean
    public CuratorFrameworkFactoryBean curatorFrameworkFactoryBean() {
        return new CuratorFrameworkFactoryBean(properties.getHost(), new ExponentialBackoffRetry(5000, 3));
    }

    /**
     * 配置zk分布式锁的注册器
     * @return
     */
    @Bean
    public LockRegistry zookeeperLockRegistry(CuratorFramework curatorFramework) {
        ZookeeperLockRegistry lockRegistry = new ZookeeperLockRegistry(curatorFramework);
        log.info("the zookeeper lock is loaded successfully");
        return lockRegistry;
    }
}
