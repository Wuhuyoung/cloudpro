package com.cloud.pro.storage.engine.local.initializer;

import com.cloud.pro.storage.engine.local.config.LocalStorageEngineConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * 初始化上传文件根目录和分片文件存储根目录的初始化器
 */
@Component
@Slf4j
// CommandLineRunner接口是Spring Boot启动后执行一些初始化操作的函数式接口
// 它提供了一个run方法，该方法在应用程序启动后被调用
public class UploadFolderAndChunksFolderInitializer implements CommandLineRunner {

    @Resource
    private LocalStorageEngineConfig config;

    @Override
    public void run(String... args) throws Exception {
        FileUtils.forceMkdir(new File(config.getRootFilePath()));
        log.info("the root file path has been created!");
        FileUtils.forceMkdir(new File(config.getRootFileChunkPath()));
        log.info("the root chunk path has been created!");
    }
}
