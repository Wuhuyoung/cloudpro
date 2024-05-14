package com.cloud.pro.server.common.schedule.launcher;

import com.cloud.pro.schedule.ScheduleManager;
import com.cloud.pro.server.common.schedule.task.RebuildShareSimpleDetailBloomFilterTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 重建查询简单分享详情布隆过滤器的任务触发器
 */
@Component
@Slf4j
public class RebuildShareSimpleDetailBloomFilterTaskLauncher implements CommandLineRunner {
    // 每天00:05执行
    private static final String CRON = "0 5 0 * * ? ";

    @Resource
    private ScheduleManager scheduleManager;

    @Resource
    private RebuildShareSimpleDetailBloomFilterTask task;

    @Override
    public void run(String... args) throws Exception {
        scheduleManager.startTask(task, CRON);
    }
}
