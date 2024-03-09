package com.cloud.pro.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SimpleScheduleTask implements ScheduleTask {
    @Override
    public String getName() {
        return "测试任务";
    }

    @Override
    public void run() {
        log.info(getName() + "正在执行中...");
    }
}
