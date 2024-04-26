package com.cloud.pro.server.modules.service.cache;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.pro.server.common.cache.AbstractManualCacheService;
import com.cloud.pro.server.modules.entity.Share;
import com.cloud.pro.server.modules.mapper.ShareMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 手动缓存实现分享业务的查询等操作
 */
@Component(value = "shareManualCacheService")
public class ShareCacheService extends AbstractManualCacheService<Share> {

    @Resource
    private ShareMapper shareMapper;

    @Override
    protected BaseMapper<Share> getBaseMapper() {
        return shareMapper;
    }

    @Override
    public String getKeyFormat() {
        return "SHARE:ID:%s";
    }
}
