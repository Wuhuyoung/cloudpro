package com.cloud.pro.server.common.cache;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.pro.cache.core.constants.CacheConstants;
import com.cloud.pro.core.exception.BusinessException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.assertj.core.util.Lists;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 手动处理缓存的公用顶级父类
 * @param <V>
 */
public abstract class AbstractManualCacheService<V> implements ManualCacheService<V> {

    @Resource
    private CacheManager cacheManager;

    private final Object lock = new Object(); // 暂时用本地锁解决缓存击穿问题

    protected abstract BaseMapper<V> getBaseMapper();

    @Override
    public V getById(Serializable id) {
        V result = getByCache(id);
        if (Objects.nonNull(result)) {
            return result;
        }
        // 使用锁解决缓存击穿，只有一个线程去查询数据库
        synchronized (lock) {
            result = getByCache(id);
            if (Objects.nonNull(result)) {
                return result;
            }
            result = getByDB(id);
            if (Objects.nonNull(result)) {
                putCache(id, result);
            }
        }
        return result;
    }

    @Override
    public boolean updateById(Serializable id, V entity) {
        int rowNum = getBaseMapper().updateById(entity);
        removeCache(id);
        return rowNum == 1;
    }

    @Override
    public boolean removeById(Serializable id) {
        int rowNum = getBaseMapper().deleteById(id);
        removeCache(id);
        return rowNum == 1;
    }

    @Override
    public List<V> getByIds(Collection<? extends Serializable> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        return ids.stream().map(this::getById).collect(Collectors.toList());
    }

    @Override
    public boolean updateByIds(Map<? extends Serializable, V> entityMap) {
        if (MapUtils.isEmpty(entityMap)) {
            return true;
        }
        for (Map.Entry<? extends Serializable, V> entry : entityMap.entrySet()) {
            if (!updateById(entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return true;
        }
        for (Serializable id : ids) {
            if (!removeById(id)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取缓存实体
     * @return
     */
    @Override
    public Cache getCache() {
        if (Objects.isNull(cacheManager)) {
            throw new BusinessException("the cache manager is empty!");
        }
        return cacheManager.getCache(CacheConstants.CACHE_NAME);
    }

    /**********************************private**********************************/

    /**
     * 删除缓存信息
     * @param id
     */
    private void removeCache(Serializable id) {
        String cacheKey = getCacheKey(id);
        Cache cache = getCache();
        if (Objects.isNull(cache)) {
            return;
        }
        cache.evict(cacheKey);
    }

    /**
     * 将实体信息保存到缓存中
     * @param id
     * @param entity
     */
    private void putCache(Serializable id, V entity) {
        Cache cache = getCache();
        if (Objects.isNull(cache)) {
            return;
        }
        String cacheKey = getCacheKey(id);
        if (Objects.isNull(entity)) {
            return;
        }
        cache.put(cacheKey, entity);
    }

    /**
     * 根据ID从数据库中查询对应的实体
     * @param id
     * @return
     */
    private V getByDB(Serializable id) {
        return getBaseMapper().selectById(id);
    }

    /**
     * 根据ID从缓存中查询对应的实体
     * @param id
     * @return
     */
    private V getByCache(Serializable id) {
        String cacheKey = getCacheKey(id);
        Cache cache = getCache();
        if (Objects.isNull(cache)) {
            return null;
        }
        Cache.ValueWrapper valueWrapper = cache.get(cacheKey);
        if (Objects.isNull(valueWrapper)) {
            return null;
        }
        return (V) valueWrapper.get();
    }

    /**
     * 生成对应的缓存key
     * @param id
     * @return
     */
    private String getCacheKey(Serializable id) {
        return String.format(getKeyFormat(), id);
    }
}
