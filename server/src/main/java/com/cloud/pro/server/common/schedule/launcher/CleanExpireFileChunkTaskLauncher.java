package com.cloud.pro.server.common.schedule.launcher;

import com.cloud.pro.schedule.ScheduleManager;
import com.cloud.pro.server.common.schedule.task.CleanExpireChunkFileTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时清理过期文件分片的任务触发器
 */
@Component
@Slf4j
public class CleanExpireFileChunkTaskLauncher implements CommandLineRunner {
    // 每天00:10执行
    private static final String CRON = "0 10 0 * * ? ";

    // 每5秒执行一次
//    private static final String CRON = "0/5 * * * * ? ";

    @Resource
    private ScheduleManager scheduleManager;

    @Resource
    private CleanExpireChunkFileTask task;

    @Override
    public void run(String... args) throws Exception {
        scheduleManager.startTask(task, CRON);
    }
}
