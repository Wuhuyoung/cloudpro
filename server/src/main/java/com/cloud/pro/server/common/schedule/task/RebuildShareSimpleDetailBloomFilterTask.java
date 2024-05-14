package com.cloud.pro.server.common.schedule.task;

import com.cloud.pro.bloom.filter.core.BloomFilter;
import com.cloud.pro.bloom.filter.core.BloomFilterManager;
import com.cloud.pro.schedule.ScheduleTask;
import com.cloud.pro.server.modules.service.ShareService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 重建查询简单分享详情布隆过滤器的任务
 */
@Component
@Slf4j
public class RebuildShareSimpleDetailBloomFilterTask implements ScheduleTask {
    @Resource
    private BloomFilterManager manager;

    private static final String BLOOM_FILTER_NAME = "SHARE_SIMPLE_DETAIL";

    @Resource
    private ShareService shareService;

    @Override
    public String getName() {
        return "RebuildShareSimpleDetailBloomFilterTask";
    }

    /**
     * 执行重建任务
     */
    @Override
    public void run() {
        log.info("start rebuild ShareSimpleDetailBloomFilter...");
        BloomFilter<Long> bloomFilter = manager.getFilter(BLOOM_FILTER_NAME);
        if (Objects.isNull(bloomFilter)) {
            log.info("the bloom filter named {} is null, give up rebuild", BLOOM_FILTER_NAME);
            return;
        }
        bloomFilter.clear();

        // 滚动查询shareId放入布隆过滤器中
        long startId = 0L;
        long batchSize = 10000L;
        List<Long> shareIdList;
        AtomicInteger addCount = new AtomicInteger(0);
        do {
            shareIdList = shareService.rollingQueryShareId(startId, batchSize);
            if (CollectionUtils.isNotEmpty(shareIdList)) {
                shareIdList.forEach(shareId -> {
                    bloomFilter.put(shareId);
                    addCount.incrementAndGet();
                });
                startId = shareIdList.get(shareIdList.size() - 1);
            }
        } while (CollectionUtils.isNotEmpty(shareIdList));

        log.info("finish rebuild ShareSimpleDetailBloomFilter, total set item count {}.", addCount.get());
    }
}
