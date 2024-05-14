package com.cloud.pro.bloom.filter.local;

import com.cloud.pro.bloom.filter.core.BloomFilter;
import com.cloud.pro.bloom.filter.core.BloomFilterManager;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 本地布隆过滤器的管理器
 */
@Component
public class LocalBloomFilterManager implements BloomFilterManager, InitializingBean {

    @Resource
    private LocalBloomFilterConfig config;

    private Map<String, BloomFilter> bloomFilterContainer = Maps.newConcurrentMap();

    @Override
    public BloomFilter getFilter(String name) {
        return bloomFilterContainer.get(name);
    }

    @Override
    public Collection<String> getFilterNames() {
        return bloomFilterContainer.keySet();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<LocalBloomFilterConfigItem> items = config.getItems();
        if (CollectionUtils.isNotEmpty(items)) {
            items.stream().forEach(item -> {
                String funnelTypeName = item.getFunnelTypeName();
                try {
                    FunnelType funnelType = FunnelType.valueOf(funnelTypeName);
                    if (Objects.nonNull(funnelType)) {
                        bloomFilterContainer.putIfAbsent(
                                item.getName(),
                                new LocalBloomFilter(funnelType.getFunnel(), item.getExpectedInsertions(), item.getFpp()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
