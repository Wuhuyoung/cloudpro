package com.cloud.pro.schedule;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@SpringBootApplication
public class ScheduleTaskTest {
    @Resource
    private ScheduleManager manager;

    @Resource
    private SimpleScheduleTask task;

    @Test
    public void testRunScheduleTask() throws Exception {
        // 每5秒执行一次
        String cron = "0/5 * * * * ? ";

        String key = manager.startTask(task, cron);

        Thread.sleep(10000L);

        cron = "0/1 * * * * ? ";
        key = manager.changeTask(key, cron);

        Thread.sleep(10000L);

        manager.stopTask(key);
    }
}
