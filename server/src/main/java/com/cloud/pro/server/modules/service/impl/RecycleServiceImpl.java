package com.cloud.pro.server.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.pro.core.constants.CommonConstants;
import com.cloud.pro.core.exception.BusinessException;
import com.cloud.pro.server.common.event.file.FilePhysicalDeleteEvent;
import com.cloud.pro.server.common.event.file.FileRestoreEvent;
import com.cloud.pro.server.enums.DelFlagEnum;
import com.cloud.pro.server.modules.context.file.QueryFileListContext;
import com.cloud.pro.server.modules.context.recycle.DeleteContext;
import com.cloud.pro.server.modules.context.recycle.QueryRecycleFileListContext;
import com.cloud.pro.server.modules.context.recycle.RestoreContext;
import com.cloud.pro.server.modules.entity.File;
import com.cloud.pro.server.modules.entity.UserFile;
import com.cloud.pro.server.modules.service.FileService;
import com.cloud.pro.server.modules.service.RecycleService;
import com.cloud.pro.server.modules.service.UserFileService;
import com.cloud.pro.server.modules.vo.UserFileVO;
import com.cloud.pro.storage.engine.core.StorageEngine;
import com.cloud.pro.storage.engine.core.context.DeleteFileContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 回收站模块业务处理类
 */
@Service
public class RecycleServiceImpl implements RecycleService, ApplicationContextAware {

    @Resource
    private UserFileService userFileService;

    @Resource
    private FileService fileService;

    @Resource
    private StorageEngine storageEngine;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 查询回收站文件列表
     *
     * @param context
     * @return
     */
    @Override
    public List<UserFileVO> recycles(QueryRecycleFileListContext context) {
        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setUserId(context.getUserId());
        queryFileListContext.setDelFlag(DelFlagEnum.YES.getCode());
        return userFileService.getFileList(queryFileListContext);
    }

    /**
     * 文件还原
     *
     * @param context
     */
    @Override
    public void restore(RestoreContext context) {
        List<Long> fileIdList = context.getFileIdList();
        Long userId = context.getUserId();
        // 1.检查用户操作权限
        List<UserFile> records = userFileService.listByIds(fileIdList);
        if (CollectionUtils.isEmpty(records)) {
            throw new BusinessException("文件还原失败");
        }
        Set<Long> userIdSet = records.stream().map(UserFile::getUserId).collect(Collectors.toSet());
        if (userIdSet.size() > 1 || !userIdSet.contains(userId)) {
            throw new BusinessException("您无权执行文件还原");
        }
        // 2.检查是不是可以还原（检查要还原的文件名称是否被占用）
        checkRestoreFilename(records);
        // 3.执行文件还原
        records.forEach(record -> {
            record.setDelFlag(DelFlagEnum.NO.getCode());
            record.setUpdateUser(userId);
        });
        if (!userFileService.updateBatchById(records)) {
            throw new BusinessException("文件还原失败");
        }
        // 4.文件还原的后置操作
        FileRestoreEvent event = new FileRestoreEvent(this, fileIdList);
        applicationContext.publishEvent(event);
    }

    /**
     * 文件彻底删除
     *
     * 1.校验用户权限
     * 2.递归查询所有要被删除的文件记录
     * 3.删除
     * 4.删除的后置动作
     *
     * @param context
     */
    @Override
    public void delete(DeleteContext context) {
        checkFileDeletePermission(context);

        List<UserFile> records = context.getRecords();
        context.setAllRecords(userFileService.findAllFileRecords(records, DelFlagEnum.YES_AND_NO));

        doDelete(context);
        afterDelete(context);
    }

    /**************************************private**************************************/

    private void afterDelete(DeleteContext context) {
        // 1.发送一个文件彻底删除的事件
        FilePhysicalDeleteEvent event = new FilePhysicalDeleteEvent(this, context.getAllRecords());
        applicationContext.publishEvent(event);
    }

    private void doDelete(DeleteContext context) {
        // 1.删除物理文件
        // 2.删除文件记录
        // 这里需要注意，可能同一份物理文件和文件记录对应多份用户文件，所以不能轻易删除，因为不知道是否是最后一个用户文件
        // 3.删除用户文件记录
        List<UserFile> allRecords = context.getAllRecords();
        List<Long> fileIdList = allRecords.stream().map(UserFile::getFileId).collect(Collectors.toList());
        if (!userFileService.removeByIds(fileIdList)) {
            throw new BusinessException("文件删除失败");
        }
    }

    /**
     * 校验文件删除的权限
     * @param context
     */
    private void checkFileDeletePermission(DeleteContext context) {
        List<Long> fileIdList = context.getFileIdList();
        Long userId = context.getUserId();
        List<UserFile> records = userFileService.listByIds(fileIdList);
        if (CollectionUtils.isEmpty(records)) {
            throw new BusinessException("文件删除失败，文件不存在");
        }
        Set<Long> userIdSet = records.stream().map(UserFile::getUserId).collect(Collectors.toSet());
        if (userIdSet.size() > 1 || !userIdSet.contains(userId)) {
            throw new BusinessException("您无权执行文件删除");
        }
        context.setRecords(records);
    }

    /**
     * 检查要还原的文件名称是否被占用
     *
     * 1.要还原的文件列表中有同一个文件夹下面文件名称相同的文件，不允许还原
     * 2.要还原的文件当前文件夹下存在同名文件，不允许还原
     *
     * @param records
     */
    private void checkRestoreFilename(List<UserFile> records) {
        Set<String> filenameSet = records.stream().map(record -> record.getFilename() + CommonConstants.COMMON_SEPARATOR + record.getParentId())
                .collect(Collectors.toSet());
        if (filenameSet.size() != records.size()) {
            throw new BusinessException("还原失败，存在同名文件，请逐个还原并重命名");
        }
        for (UserFile record : records) {
            LambdaQueryWrapper<UserFile> lqw = new LambdaQueryWrapper<>();
            lqw.eq(UserFile::getUserId, record.getUserId());
            lqw.eq(UserFile::getParentId, record.getParentId());
            lqw.eq(UserFile::getFilename, record.getFilename());
            lqw.eq(UserFile::getDelFlag, DelFlagEnum.NO.getCode());
            long count = userFileService.count(lqw);
            if (count > 0) {
                throw new BusinessException("文件：" + record.getFilename() + " 还原失败，该文件夹下存在同名的文件或文件夹");
            }
        }
    }
}
