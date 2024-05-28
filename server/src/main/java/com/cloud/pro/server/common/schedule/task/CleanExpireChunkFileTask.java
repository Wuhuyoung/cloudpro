package com.cloud.pro.server.common.schedule.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.pro.core.constants.CommonConstants;
import com.cloud.pro.schedule.ScheduleTask;
import com.cloud.pro.server.common.stream.channel.CloudProChannels;
import com.cloud.pro.server.common.stream.event.log.ErrorLogEvent;
import com.cloud.pro.server.modules.entity.FileChunk;
import com.cloud.pro.server.modules.service.FileChunkService;
import com.cloud.pro.storage.engine.core.StorageEngine;
import com.cloud.pro.storage.engine.core.context.DeleteFileContext;
import com.cloud.pro.stream.core.IStreamProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 过期分片清理任务
 */
@Component
@Slf4j
public class CleanExpireChunkFileTask implements ScheduleTask {

    @Resource
    private FileChunkService fileChunkService;

    @Resource
    private StorageEngine storageEngine;

    private static final Long BATCH_SIZE = 500L;

    @Resource(name = "defaultStreamProducer")
    private IStreamProducer producer;

    @Override
    public String getName() {
        return "CleanExpireChunkFileTask";
    }

    /**
     * 执行清理任务
     */
    @Override
    public void run() {
        // 1.滚动查询过期的文件分片
        // 2.删除物理文件（文件存储引擎实现）
        // 3.删除过期文件分片的记录
        // 4.重置上次查询的最大文件分片记录ID，继续滚动查询
        log.info("{} start clean expire chunk file...", getName());
        List<FileChunk> expireChunkFileRecords;
        long scrollPointer = 1L;

        do {
            // 滚动查询过期的文件分片
            expireChunkFileRecords = scrollQueryExpireChunkFileRecords(scrollPointer);
            if (CollectionUtils.isNotEmpty(expireChunkFileRecords)) {
                // 删除物理文件
                deleteRealChunkFiles(expireChunkFileRecords);
                // 删除过期文件分片的记录
                List<Long> idList = deleteChunkFileRecords(expireChunkFileRecords);
                // 重置上次查询的最大文件分片记录ID
                scrollPointer = Collections.max(idList) + 1L;
            }
        } while (CollectionUtils.isNotEmpty(expireChunkFileRecords));

        log.info("{} finish clean expire chunk file...", getName());
    }

    /**********************************private**********************************/

    /**
     * 滚动查询过期的文件分片记录（不能一次性查询出来，当分片过多内存中无法加载）
     * 主键是自增的，可以使用滚动查询
     * @param scrollPointer
     * @return
     */
    private List<FileChunk> scrollQueryExpireChunkFileRecords(Long scrollPointer) {
        LambdaQueryWrapper<FileChunk> lqw = new LambdaQueryWrapper<>();
        lqw.le(FileChunk::getExpirationTime, LocalDateTime.now());
        lqw.ge(FileChunk::getId, scrollPointer);
        lqw.last(" limit " + BATCH_SIZE);
        return fileChunkService.list(lqw);
    }

    /**
     * 物理删除过期的文件分片实体
     * @param expireChunkFileRecords
     */
    private void deleteRealChunkFiles(List<FileChunk> expireChunkFileRecords) {
        DeleteFileContext context = new DeleteFileContext();
        List<String> realPathList = expireChunkFileRecords.stream().map(FileChunk::getRealPath).collect(Collectors.toList());
        context.setRealFilePathList(realPathList);
        try {
            storageEngine.delete(context);
        } catch (IOException e) {
            ErrorLogEvent event = new ErrorLogEvent("分片物理文件删除失败，请手动执行删除！文件路径为：" + JSON.toJSONString(realPathList),
                    CommonConstants.ZERO_LONG);
            producer.sendMessage(CloudProChannels.ERROR_LOG_OUTPUT, event);
        }
    }

    /**
     * 删除过期的文件分片记录
     * @param expireChunkFileRecords
     * @return
     */
    private List<Long> deleteChunkFileRecords(List<FileChunk> expireChunkFileRecords) {
        List<Long> expireIds = expireChunkFileRecords.stream().map(FileChunk::getId).collect(Collectors.toList());
        fileChunkService.removeByIds(expireIds);
        return expireIds;
    }
}
