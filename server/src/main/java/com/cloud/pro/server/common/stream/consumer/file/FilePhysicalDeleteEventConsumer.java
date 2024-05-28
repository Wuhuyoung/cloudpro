package com.cloud.pro.server.common.stream.consumer.file;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.pro.core.constants.CommonConstants;
import com.cloud.pro.server.common.stream.event.log.ErrorLogEvent;
import com.cloud.pro.server.common.stream.channel.CloudProChannels;
import com.cloud.pro.server.common.stream.event.file.FilePhysicalDeleteEvent;
import com.cloud.pro.server.enums.FolderFlagEnum;
import com.cloud.pro.server.modules.entity.File;
import com.cloud.pro.server.modules.entity.UserFile;
import com.cloud.pro.server.modules.service.FileService;
import com.cloud.pro.server.modules.service.UserFileService;
import com.cloud.pro.storage.engine.core.StorageEngine;
import com.cloud.pro.storage.engine.core.context.DeleteFileContext;
import com.cloud.pro.stream.core.AbstractConsumer;
import com.cloud.pro.stream.core.IStreamProducer;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 文件物理删除监听器
 */
@Component
public class FilePhysicalDeleteEventConsumer extends AbstractConsumer {
    @Resource
    private UserFileService userFileService;

    @Resource
    private FileService fileService;

    @Resource
    private StorageEngine storageEngine;

    @Resource(name = "defaultStreamProducer")
    private IStreamProducer producer;


    /**
     * 资源释放器，释放被物理删除的文件列表中关联的文件记录
     * @param message
     */
    @StreamListener(CloudProChannels.PHYSICAL_DELETE_FILE_INPUT)
    public void physicalDeleteFile(Message<FilePhysicalDeleteEvent> message) {
        if (isEmptyMessage(message)) {
            return;
        }
        printLog(message);
        FilePhysicalDeleteEvent event = message.getPayload();
        List<UserFile> records = event.getAllRecords();
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        // 1.查询所有无引用的实体文件记录
        List<Long> deleteRealFileIds = findAllUnusedRealFileIdList(records);
        if (CollectionUtils.isEmpty(deleteRealFileIds)) {
            return;
        }
        // 2.删除文件记录
        List<File> fileList = fileService.listByIds(deleteRealFileIds);
        if (CollectionUtils.isEmpty(fileList)) {
            return;
        }
        if (!fileService.removeByIds(deleteRealFileIds)) {
            ErrorLogEvent logEvent = new ErrorLogEvent("实体文件记录ID：" + JSON.toJSONString(deleteRealFileIds) + " 物理删除失败，请手动执行删除", CommonConstants.ZERO_LONG);
            producer.sendMessage(CloudProChannels.ERROR_LOG_OUTPUT, logEvent);
            return;
        }
        // 3.删除物理文件（委托文件存储引擎）
        physicalDeleteFileByStorageEngine(fileList);
    }

    /**************************************private**************************************/

    /**
     * 委托文件存储引擎执行物理文件的删除
     * @param fileList
     */
    private void physicalDeleteFileByStorageEngine(List<File> fileList) {
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<String> realPathList = fileList.stream().map(File::getRealPath).collect(Collectors.toList());
        deleteFileContext.setRealFilePathList(realPathList);
        try {
            storageEngine.delete(deleteFileContext);
        } catch (IOException e) {
            ErrorLogEvent logEvent = new ErrorLogEvent("实体文件路径：" + JSON.toJSONString(realPathList) + " 物理删除失败，请手动执行删除", CommonConstants.ZERO_LONG);
            producer.sendMessage(CloudProChannels.ERROR_LOG_OUTPUT, logEvent);
        }
    }

    /**
     * 查找所有 没有被引用的真实文件记录ID集合
     * @param records
     * @return
     */
    private List<Long> findAllUnusedRealFileIdList(List<UserFile> records) {
        List<Long> deleteRealFileId = Lists.newArrayList();
        records.stream()
                .filter(record -> Objects.equals(FolderFlagEnum.NO.getCode(), record.getFolderFlag()))
                .forEach(record -> {
                    Long realFileId = record.getRealFileId();
                    LambdaQueryWrapper<UserFile> lqw = new LambdaQueryWrapper<>();
                    lqw.eq(UserFile::getRealFileId, realFileId);
                    long count = userFileService.count(lqw);
                    if (count == 0) {
                        // 该文件记录没有关联的用户文件记录了，可以删除
                        deleteRealFileId.add(realFileId);
                    }
                });
        return deleteRealFileId;
    }
}
