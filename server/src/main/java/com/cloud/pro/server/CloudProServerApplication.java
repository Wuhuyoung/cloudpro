package com.cloud.pro.server;

import com.cloud.pro.core.constants.CommonConstants;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = CommonConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = CommonConstants.BASE_COMPONENT_SCAN_PATH)
@EnableTransactionManagement
@MapperScan(basePackages = CommonConstants.BASE_COMPONENT_SCAN_PATH + ".server.modules.**.mapper")
public class CloudProServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudProServerApplication.class, args);
    }
}
