package com.cloud.pro.server.modules.service.cache;

import com.cloud.pro.cache.core.constants.CacheConstants;
import com.cloud.pro.server.common.cache.AnnotationCacheService;
import com.cloud.pro.server.modules.entity.User;
import com.cloud.pro.server.modules.mapper.UserMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * 注解缓存实现用户模块的查询等操作
 */
@Component(value = "userAnnotationCacheService")
public class UserCacheService implements AnnotationCacheService<User> {

    @Resource
    private UserMapper userMapper;

    // sync=true，实际查询数据库也是通过本地锁synchronized来保证只有一个线程进入
    @Cacheable(cacheNames = CacheConstants.CACHE_NAME,
                keyGenerator = "userIdKeyGenerator",
                sync = true)
    @Override
    public User getById(Serializable id) {
        return userMapper.selectById(id);
    }

    @CachePut(cacheNames = CacheConstants.CACHE_NAME, keyGenerator = "userIdKeyGenerator")
    @Override
    public boolean updateById(Serializable id, User entity) {
        return userMapper.updateById(entity) == 1;
    }

    @CacheEvict(cacheNames = CacheConstants.CACHE_NAME, keyGenerator = "userIdKeyGenerator")
    @Override
    public boolean removeById(Serializable id) {
        return userMapper.deleteById(id) == 1;
    }


}
