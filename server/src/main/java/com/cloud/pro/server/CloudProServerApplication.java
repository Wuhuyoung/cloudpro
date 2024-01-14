package com.cloud.pro.server;

import com.cloud.pro.core.constants.CommonConstants;
import com.cloud.pro.core.response.Result;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = CommonConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = CommonConstants.BASE_COMPONENT_SCAN_PATH)
@RestController
public class CloudProServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudProServerApplication.class, args);
    }

    @GetMapping("/hello")
    public Result<String> hello(String name) {
        return Result.success("hello, " + name);
    }
}
