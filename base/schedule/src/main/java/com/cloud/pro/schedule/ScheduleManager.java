package com.cloud.pro.schedule;

import com.cloud.pro.core.exception.FrameworkException;
import com.cloud.pro.core.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 定时任务管理器
 */
@Component
@Slf4j
public class ScheduleManager {
    @Resource
    private ThreadPoolTaskScheduler taskScheduler;

    /**
     * 定时任务缓存
     */
    private Map<String, ScheduleTaskHolder> cache = new ConcurrentHashMap<>();

    /**
     * 启动一个定时任务
     * @param scheduleTask
     * @param cron
     * @return 定时任务的唯一标识
     */
    public String startTask(ScheduleTask scheduleTask, String cron) {
        if (Objects.isNull(scheduleTask) || StringUtils.isBlank(cron)) {
            throw new FrameworkException("定时任务和cron表达式不能为空");
        }
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(scheduleTask, new CronTrigger(cron));
        String key = UUIDUtil.getUUID();
        ScheduleTaskHolder holder = new ScheduleTaskHolder(scheduleTask, scheduledFuture);
        cache.put(key, holder);
        log.info("{} 启动成功！唯一标识为: {}", scheduleTask.getName(), key);
        return key;
    }

    /**
     * 停止一个定时任务
     * @param key 定时任务的唯一标识
     */
    public void stopTask(String key) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        ScheduleTaskHolder holder = cache.get(key);
        if (Objects.isNull(holder)) {
            return;
        }
        ScheduledFuture scheduledFuture = holder.getScheduledFuture();
        boolean cancel = scheduledFuture.cancel(true);
        if (cancel) {
            cache.remove(key);
            log.info("{} 停止成功！唯一标识为: {}", holder.getScheduleTask().getName(), key);
        } else {
            log.warn("{} 停止失败！唯一标识为: {}", holder.getScheduleTask().getName(), key);
        }
    }

    /**
     * 更新一个定时任务的执行时间
     * @param key 定时任务的唯一标识
     * @param cron 新的cron表达式
     * @return
     */
    public String changeTask(String key, String cron) {
        if (StringUtils.isAnyBlank(key, cron)) {
            throw new FrameworkException("定时任务的key和cron表达式不能为空");
        }
        ScheduleTaskHolder holder = cache.get(key);
        if (Objects.isNull(holder)) {
            throw new FrameworkException(key + "对应的定时任务不存在");
        }
        stopTask(key);
        return startTask(holder.getScheduleTask(), cron);
    }
}
