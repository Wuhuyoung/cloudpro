package com.cloud.pro.server.test.controller;

import com.cloud.pro.server.common.annotation.LoginIgnore;
import com.cloud.pro.server.common.event.test.TestEvent;
import io.swagger.annotations.Api;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
@Api(tags = "测试模块")
public class TestController implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 测试事件发布
     * @return
     */
    @GetMapping("/test")
    @LoginIgnore
    public String testEventPublish() {
        applicationContext.publishEvent(new TestEvent(this, "test"));
        return "done";
    }
}
