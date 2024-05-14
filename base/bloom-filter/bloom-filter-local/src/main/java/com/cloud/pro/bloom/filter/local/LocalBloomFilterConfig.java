package com.cloud.pro.bloom.filter.local;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 本地布隆过滤器配置类
 */
@Component
@Data
@ConfigurationProperties(prefix = "com.cloud.pro.bloom.filter.local")
public class LocalBloomFilterConfig {

    private List<LocalBloomFilterConfigItem> items;
}
