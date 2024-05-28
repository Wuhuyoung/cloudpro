package com.cloud.pro.server;

import com.cloud.pro.core.constants.CommonConstants;
import com.cloud.pro.server.common.stream.channel.CloudProChannels;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = CommonConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = CommonConstants.BASE_COMPONENT_SCAN_PATH)
@ComponentScan(basePackages = CommonConstants.BASE_COMPONENT_SCAN_PATH)
@EnableTransactionManagement
@MapperScan(basePackages = CommonConstants.BASE_COMPONENT_SCAN_PATH + ".server.modules.mapper")
@EnableAsync
@EnableBinding(CloudProChannels.class)
@Slf4j
public class CloudProServerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(CloudProServerApplication.class, args);
        printStartLog(applicationContext);
    }

    /**
     * 项目启动成功 日志打印
     * @param applicationContext
     */
    private static void printStartLog(ConfigurableApplicationContext applicationContext) {
        String serverPort = applicationContext.getEnvironment().getProperty("server.port");
        String serverUrl = String.format("http://%s:%s", "127.0.0.1", serverPort);
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, "cloud pro started at: ", serverUrl));
        if (checkShowServerDoc(applicationContext)) {
            log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, "cloud pro's doc started at: ", serverUrl + "/doc.html"));
        }
        log.info(AnsiOutput.toString(AnsiColor.YELLOW, "cloud pro has started successfully!"));
    }

    /**
     * 校验是否开启了接口文档
     * @param applicationContext
     * @return
     */
    private static boolean checkShowServerDoc(ConfigurableApplicationContext applicationContext) {
        return applicationContext.getEnvironment().getProperty("swagger2.show", Boolean.class, true)
                && applicationContext.containsBean("swagger2Config");
    }
}
