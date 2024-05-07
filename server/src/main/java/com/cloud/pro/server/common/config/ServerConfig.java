package com.cloud.pro.server.common.config;

import com.cloud.pro.core.constants.CommonConstants;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConfigurationProperties(prefix = "com.cloud.pro.server")
@Data
public class ServerConfig {

    @Value("${server.port}")
    private Integer serverPort;

    /**
     * 文件分片的过期天数
     */
    private Integer chunkFileExpirationDays = CommonConstants.ONE_INT;

    /**
     * 分享链接的前缀
     */
    private String sharePrefix;

    @PostConstruct
    public void initSharePrefix() {
        // 由于@Value是在bean初始化之后解析，所以需要等bean初始化后才能获取到serverPort
//        sharePrefix = "http://127.0.0.1:" + serverPort + "/share/";
        sharePrefix = "http://127.0.0.1:5173/share/";
    }
}
