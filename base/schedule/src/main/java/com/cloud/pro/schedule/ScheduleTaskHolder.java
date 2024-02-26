package com.cloud.pro.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.ScheduledFuture;

/**
 * 定时任务和定时任务结果的缓存对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleTaskHolder implements Serializable {
    /**
     * 执行任务
     */
    private ScheduleTask scheduleTask;
    /**
     * 执行任务的结果
     */
    private ScheduledFuture scheduledFuture;
}
