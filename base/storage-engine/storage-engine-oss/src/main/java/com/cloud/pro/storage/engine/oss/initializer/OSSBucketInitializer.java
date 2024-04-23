package com.cloud.pro.storage.engine.oss.initializer;

import com.aliyun.oss.OSSClient;
import com.cloud.pro.core.exception.FrameworkException;
import com.cloud.pro.storage.engine.oss.config.OSSStorageEngineConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * OSS桶的初始化器
 */
@Component
@Slf4j
// CommandLineRunner接口是Spring Boot启动后执行一些初始化操作的函数式接口
// 它提供了一个run方法，该方法在应用程序启动后被调用
public class OSSBucketInitializer implements CommandLineRunner {

    @Resource
    private OSSStorageEngineConfig config;

    @Resource
    private OSSClient client;

    @Override
    public void run(String... args) throws Exception {
        boolean bucketExist = client.doesBucketExist(config.getBucketName());

        if (!bucketExist && config.getAutoCreateBucket()) {
            client.createBucket(config.getBucketName());
        }
        if (!bucketExist && !config.getAutoCreateBucket()) {
            throw new FrameworkException("the bucket " + config.getBucketName() + " is not available");
        }
        log.info("the bucket " + config.getBucketName() + " has been created");
    }
}
