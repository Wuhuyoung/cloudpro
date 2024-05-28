package com.cloud.pro.server.modules.controller;

import com.cloud.pro.server.common.annotation.LoginIgnore;
import com.cloud.pro.server.common.stream.event.test.TestEvent;
import com.cloud.pro.server.common.stream.channel.CloudProChannels;
import com.cloud.pro.stream.core.IStreamProducer;
import io.swagger.annotations.Api;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

//@RestController
@Api(tags = "测试模块")
public class TestController implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Resource(name = "defaultStreamProducer")
    private IStreamProducer producer;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

//    /**
//     * 测试事件发布
//     * @return
//     */
//    @GetMapping("/test")
//    @LoginIgnore
//    public String testEventPublish() {
//        applicationContext.publishEvent(new TestEvent(this, "test"));
//        return "done";
//    }


    /**
     * 测试流消息发送
     * @return
     */
    @GetMapping("/stream/test")
    @LoginIgnore
    public String testStream(String name) {
        com.cloud.pro.server.common.stream.event.TestEvent event = new com.cloud.pro.server.common.stream.event.TestEvent();
        event.setName(name);
        producer.sendMessage(CloudProChannels.TEST_OUTPUT, event);
        return "done";
    }
}
