package com.cloud.pro.storage.engine.fd.config;

import com.cloud.pro.core.exception.FrameworkException;
import com.github.tobato.fastdfs.conn.ConnectionPoolConfig;
import com.github.tobato.fastdfs.conn.FdfsConnectionPool;
import com.github.tobato.fastdfs.conn.PooledConnectionFactory;
import com.github.tobato.fastdfs.conn.TrackerConnectionManager;
import lombok.Data;
import org.assertj.core.util.Lists;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * FastDFS文件存储引擎配置类
 */
@Configuration
@ConfigurationProperties(prefix = "com.cloud.pro.storage.engine.fdfs")
@Data
// 解决jmx重复注册bean的问题
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@ComponentScan(value = {"com.github.tobato.fastdfs.service", "com.github.tobato.fastdfs.domain"})
public class FastDFSStorageEngineConfig {

    /**
     * 连接的超时时间
     */
    private Integer connectTimeout = 600;

    /**
     * 跟踪服务器地址列表
     */
    private List<String> trackerList = Lists.newArrayList();

    /**
     * 组名称
     */
    private String group = "group1";

    @Bean
    public PooledConnectionFactory pooledConnectionFactory() {
        PooledConnectionFactory factory = new PooledConnectionFactory();
        factory.setConnectTimeout(getConnectTimeout());
        return factory;
    }

    @Bean
    public ConnectionPoolConfig connectionPoolConfig() {
        return new ConnectionPoolConfig();
    }

    @Bean
    public FdfsConnectionPool fdfsConnectionPool(ConnectionPoolConfig config, PooledConnectionFactory factory) {
        FdfsConnectionPool fdfsConnectionPool = new FdfsConnectionPool(factory, config);
        return fdfsConnectionPool;
    }

    @Bean
    public TrackerConnectionManager trackerConnectionManager(FdfsConnectionPool fdfsConnectionPool) {
        TrackerConnectionManager manager = new TrackerConnectionManager(fdfsConnectionPool);
        if (CollectionUtils.isEmpty(getTrackerList())) {
            throw new FrameworkException("the tracker list is empty!");
        }
        manager.setTrackerList(getTrackerList());
        return manager;
    }
}
