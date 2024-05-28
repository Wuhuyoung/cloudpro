package com.cloud.pro.server.modules.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.pro.core.exception.BusinessException;
import com.cloud.pro.core.utils.FileUtil;
import com.cloud.pro.core.utils.IdUtil;
import com.cloud.pro.server.common.stream.channel.CloudProChannels;
import com.cloud.pro.server.common.stream.event.log.ErrorLogEvent;
import com.cloud.pro.server.modules.context.file.FileChunkMergeContext;
import com.cloud.pro.server.modules.context.file.FileSaveContext;
import com.cloud.pro.server.modules.context.file.QueryRealFileListContext;
import com.cloud.pro.server.modules.entity.File;
import com.cloud.pro.server.modules.entity.FileChunk;
import com.cloud.pro.server.modules.mapper.FileMapper;
import com.cloud.pro.server.modules.service.FileChunkService;
import com.cloud.pro.server.modules.service.FileService;
import com.cloud.pro.storage.engine.core.StorageEngine;
import com.cloud.pro.storage.engine.core.context.DeleteFileContext;
import com.cloud.pro.storage.engine.core.context.MergeFileContext;
import com.cloud.pro.storage.engine.core.context.StoreFileContext;
import com.cloud.pro.stream.core.IStreamProducer;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @author han
* @description 针对表【cloud_pro_file(物理文件信息表)】的数据库操作Service实现
* @createDate 2024-04-16 19:44:30
*/
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File>
    implements FileService {

    @Resource
    private StorageEngine storageEngine;

    @Resource
    private FileChunkService fileChunkService;

    @Resource(name = "defaultStreamProducer")
    private IStreamProducer producer;

    /**
     * 根据条件查询用户的实际文件列表
     * @param context
     * @return
     */
    @Override
    public List<File> getFileList(QueryRealFileListContext context) {
        Long userId = context.getUserId();
        String identifier = context.getIdentifier();
        LambdaQueryWrapper<File> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Objects.nonNull(userId), File::getCreateUser, userId);
        lqw.eq(StringUtils.isNotBlank(identifier), File::getIdentifier, identifier);
        return this.list(lqw);
    }

    /**
     * 上传单文件并保存实体记录
     * @param context
     */
    @Override
    public void saveFile(FileSaveContext context) {
        // 上传单文件，该方法委托文件存储引擎实现
        try {
            StoreFileContext storeFileContext = new StoreFileContext();
            storeFileContext.setFilename(context.getFilename());
            storeFileContext.setTotalSize(context.getTotalSize());
            storeFileContext.setInputStream(context.getFile().getInputStream());
            storageEngine.store(storeFileContext);
            context.setRealPath(storeFileContext.getRealPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("文件上传失败");
        }

        // 保存文件实体记录
        File record = doSaveFile(context.getFilename(),
                context.getRealPath(),
                context.getTotalSize(),
                context.getIdentifier(),
                context.getUserId());
        context.setRecord(record);
    }

    /**
     * 合并文件分片，保存物理文件记录
     * @param context
     */
    @Override
    public void mergeFileChunkAndSaveFile(FileChunkMergeContext context) {
        // 1.委托文件存储引擎合并文件分片
        doMergeFileChunk(context);

        // 2.保存物理文件记录
        File record = doSaveFile(context.getFilename(),
                context.getRealPath(),
                context.getTotalSize(),
                context.getIdentifier(),
                context.getUserId());
        context.setRecord(record);
    }

    /**********************************private**********************************/

    /**
     * 委托文件存储引擎合并文件分片
     * @param context
     */
    private void doMergeFileChunk(FileChunkMergeContext context) {
        // 1.查询文件分片的记录
        LambdaQueryWrapper<FileChunk> lqw = new LambdaQueryWrapper<>();
        lqw.eq(FileChunk::getIdentifier, context.getIdentifier());
        lqw.eq(FileChunk::getCreateUser, context.getUserId());
        lqw.ge(FileChunk::getExpirationTime, LocalDateTime.now());
        List<FileChunk> fileChunkList = fileChunkService.list(lqw);
        if (CollectionUtil.isEmpty(fileChunkList)) {
            throw new BusinessException("该文件未找到分片记录");
        }
        List<String> realPathList = fileChunkList.stream()
                .sorted(Comparator.comparing(FileChunk::getChunkNumber)) // 按照分片下标排序，方便后续合并
                .map(FileChunk::getRealPath)
                .collect(Collectors.toList());

        // 2.根据文件分片的记录合并物理分片
        MergeFileContext mergeFileContext = new MergeFileContext();
        mergeFileContext.setFilename(context.getFilename());
        mergeFileContext.setIdentifier(context.getIdentifier());
        mergeFileContext.setUserId(context.getUserId());
        mergeFileContext.setRealPathList(realPathList);
        try {
            storageEngine.mergeFile(mergeFileContext);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("文件分片合并失败");
        }
        // 3.将真实的物理存储路径保存到context中
        context.setRealPath(mergeFileContext.getRealPath());

        // 4.删除文件分片记录
        List<Long> fileChunkRecordIdList = fileChunkList.stream().map(FileChunk::getId).collect(Collectors.toList());
        fileChunkService.removeByIds(fileChunkRecordIdList);
    }

    /**
     * 保存实体文件记录
     * @param filename
     * @param realPath
     * @param totalSize
     * @param identifier
     * @param userId
     * @return
     */
    private File doSaveFile(String filename, String realPath, Long totalSize, String identifier, Long userId) {
        File record = new File();
        record.setFileId(IdUtil.get());
        record.setFilename(filename);
        record.setRealPath(realPath);
        record.setFileSize(String.valueOf(totalSize));
        record.setFileSizeDesc(FileUtil.byteCountToDisplaySize(totalSize));
        record.setFileSuffix(FileUtil.getFileSuffix(filename));
        // bugfix 获取文件的预览类型
        record.setFilePreviewContentType(FileUtil.getContentType(realPath));
        record.setIdentifier(identifier);
        record.setCreateUser(userId);

        if (!this.save(record)) {
            // 存储引擎删除已上传的物理文件
            try {
                DeleteFileContext deleteFileContext = new DeleteFileContext();
                deleteFileContext.setRealFilePathList(Lists.newArrayList(realPath));
                storageEngine.delete(deleteFileContext);
            } catch (IOException e) {
                e.printStackTrace();
                // 发送错误日志事件
                ErrorLogEvent errorLogEvent = new ErrorLogEvent("文件物理删除失败，请手动执行删除，文件路径：" + realPath, userId);
                producer.sendMessage(CloudProChannels.ERROR_LOG_OUTPUT, errorLogEvent);
            }
        }
        return record;
    }
}




