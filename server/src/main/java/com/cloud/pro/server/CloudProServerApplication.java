package com.cloud.pro.server;

import com.cloud.pro.core.constants.CommonConstants;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = CommonConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = CommonConstants.BASE_COMPONENT_SCAN_PATH)
@ComponentScan(basePackages = CommonConstants.BASE_COMPONENT_SCAN_PATH)
@EnableTransactionManagement
@MapperScan(basePackages = CommonConstants.BASE_COMPONENT_SCAN_PATH + ".server.modules.**.mapper")
@EnableAsync
public class CloudProServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudProServerApplication.class, args);
    }
}
