package com.cloud.pro.lock.zk;

import com.cloud.pro.lock.core.LockConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ZooKeeper分布式锁配置类
 */
@Component
@Data
@ConfigurationProperties(prefix = "com.cloud.pro.lock.zookeeper")
public class ZookeeperLockProperties {

    /**
     * zk连接地址，多个用逗号隔开
     */
    private String host = "127.0.0.1:2181";

    /**
     * zk分布式锁根目录
     */
    private String rootPath = LockConstants.CLOUD_PRO_LOCK_PATH;
}
