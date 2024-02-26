package com.cloud.pro.server;

import com.cloud.pro.core.constants.CommonConstants;
import com.cloud.pro.core.response.Result;
import io.swagger.annotations.Api;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@SpringBootApplication(scanBasePackages = CommonConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = CommonConstants.BASE_COMPONENT_SCAN_PATH)
@RestController
@Api("CloudProServer")
@Validated
@EnableTransactionManagement
@MapperScan(basePackages = CommonConstants.BASE_COMPONENT_SCAN_PATH + ".server.modules.**.mapper")
public class CloudProServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudProServerApplication.class, args);
    }

    @GetMapping("/hello")
    public Result<String> hello(@NotBlank(message = "name不能为空") String name) {
        return Result.success("hello, " + name);
    }
}
