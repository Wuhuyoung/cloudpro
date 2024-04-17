package com.cloud.pro.storage.engine.core;

import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * 文件存储引擎
 */
public abstract class AbstractStorageEngine implements StorageEngine {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 共用的获取缓存的方法
     * @return
     */
    protected RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }
}
