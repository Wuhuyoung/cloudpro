package com.cloud.pro.schedule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 定时模块配置类
 */
@Configuration
public class ScheduleConfig {

    /**
     * 定时执行器
     * @return
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);                        // 线程池大小
        taskScheduler.setThreadNamePrefix("taskExecutor-");   // 线程名称
        taskScheduler.setAwaitTerminationSeconds(60);         // 等待时长
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);  // 调度器shutdown被调用时等待当前被调度的任务完成
        return taskScheduler;
    }
}
