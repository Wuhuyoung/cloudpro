package com.cloud.pro.server.common.listener.share;

import com.cloud.pro.server.common.event.file.DeleteFileEvent;
import com.cloud.pro.server.common.event.file.FileRestoreEvent;
import com.cloud.pro.server.enums.DelFlagEnum;
import com.cloud.pro.server.modules.entity.UserFile;
import com.cloud.pro.server.modules.service.ShareService;
import com.cloud.pro.server.modules.service.UserFileService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 监听文件状态变更导致分享状态变更的监听器
 */
@Component
public class ShareStatusChangeListener {

    @Resource
    private ShareService shareService;

    @Resource
    private UserFileService userFileService;

    /**
     * 监听文件被删除之后，刷新所有受影响的分享的状态
     *
     * @param event
     */
    @EventListener(DeleteFileEvent.class)
    @Async(value = "eventListenerTaskExecutor")
    public void changeShare2FileDeleted(DeleteFileEvent event) {
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
     * @param event
     */
    @EventListener(FileRestoreEvent.class)
    @Async(value = "eventListenerTaskExecutor")
    public void changeShare2Normal(FileRestoreEvent event) {
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
