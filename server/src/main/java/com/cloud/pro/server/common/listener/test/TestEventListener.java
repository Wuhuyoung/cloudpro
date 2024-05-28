//package com.cloud.pro.server.common.listener.test;
//
//import com.cloud.pro.server.common.stream.event.test.TestEvent;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
///**
// * 测试事件监听器
// */
//@Component
//@Slf4j
//public class TestEventListener {
//
//    /**
//     * 监听测试事件
//     * @param event
//     */
//    @EventListener(TestEvent.class)
//    @Async(value = "eventListenerTaskExecutor")
//    public void test(TestEvent event) {
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        log.info("TestEventListener start process, the thread name is {}", Thread.currentThread().getName());
//    }
//}
