package com.cloud.pro.storage.engine.local.config;

import com.cloud.pro.core.utils.FileUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 本地文件存储引擎配置类
 */
@Component
@ConfigurationProperties(prefix = "com.cloud.pro.storage.engine.local")
@Data
public class LocalStorageEngineConfig {
    /**
     * 实际存放路径的前缀
     */
    private String rootFilePath = FileUtil.generateDefaultStoreFileRealPath();

    /**
     * 实际存放文件分片的路径的前缀
     */
    private String rootFileChunkPath = FileUtil.generateDefaultStoreFileChunkRealPath();
}
