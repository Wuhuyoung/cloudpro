package com.cloud.pro.server.common.config;

import com.cloud.pro.core.constants.CommonConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "com.cloud.pro.server")
@Data
public class ServerConfig {

    /**
     * 文件分片的过期天数
     */
    private Integer chunkFileExpirationDays = CommonConstants.ONE_INT;

    /**
     * 分享链接的前缀，注意这里是前端的地址
     */
    private String sharePrefix;
}
