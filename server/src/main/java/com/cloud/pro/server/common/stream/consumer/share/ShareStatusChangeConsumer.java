package com.cloud.pro.server.common.stream.consumer.share;

import com.cloud.pro.server.common.stream.channel.CloudProChannels;
import com.cloud.pro.server.common.stream.event.file.DeleteFileEvent;
import com.cloud.pro.server.common.stream.event.file.FileRestoreEvent;
import com.cloud.pro.server.enums.DelFlagEnum;
import com.cloud.pro.server.modules.entity.UserFile;
import com.cloud.pro.server.modules.service.ShareService;
import com.cloud.pro.server.modules.service.UserFileService;
import com.cloud.pro.stream.core.AbstractConsumer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 监听文件状态变更导致分享状态变更的监听器
 */
@Component
public class ShareStatusChangeConsumer extends AbstractConsumer {

    @Resource
    private ShareService shareService;

    @Resource
    private UserFileService userFileService;

    /**
     * 监听文件被删除之后，刷新所有受影响的分享的状态
     *
     * @param message
     */
    @StreamListener(CloudProChannels.DELETE_FILE_INPUT)
    public void changeShare2FileDeleted(Message<DeleteFileEvent> message) {
        if (isEmptyMessage(message)) {
            return;
        }
        printLog(message);
        DeleteFileEvent event = message.getPayload();
        List<Long> fileIdList = event.getFileIdList();
        if (CollectionUtils.isEmpty(fileIdList)) {
            return;
        }
        // 由于我们之前删除文件，只是标记了文件夹的删除状态，并没有把他下面的所有文件都标记删除，这样就可以判断下面所有未删除的文件都是本次受影响的文件
        // 查询fileIdList下所有未删除的文件，包括fileIdList自己(虽然自己已经被删除，但是这个方法的递归逻辑是包含自己的)
        List<UserFile> allRecords = userFileService.findAllFileRecordsByFileIdList(fileIdList, DelFlagEnum.NO);
        List<Long> allRecordIdList = allRecords.stream().map(UserFile::getFileId).collect(Collectors.toList());
        // 刷新受影响的对应的分享状态
        shareService.refreshShareStatus(allRecordIdList);
    }

    /**
     * 监听文件被还原之后，刷新所有受影响的分享的状态
     *
     * @param message
     */
    @StreamListener(CloudProChannels.FILE_RESTORE_INPUT)
    public void changeShare2Normal(Message<FileRestoreEvent> message) {
        if (isEmptyMessage(message)) {
            return;
        }
        printLog(message);
        FileRestoreEvent event = message.getPayload();

        List<Long> fileIdList = event.getFileIdList();
        if (CollectionUtils.isEmpty(fileIdList)) {
            return;
        }
        // 查询fileIdList下所有未删除的文件，包括fileIdList自己
        List<UserFile> allRecords = userFileService.findAllFileRecordsByFileIdList(fileIdList, DelFlagEnum.NO);
        List<Long> allRecordIdList = allRecords.stream().map(UserFile::getFileId).collect(Collectors.toList());
        // 刷新受影响的对应的分享状态
        shareService.refreshShareStatus(allRecordIdList);
    }
}
