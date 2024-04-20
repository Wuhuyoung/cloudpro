package com.cloud.pro.server.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.pro.core.exception.BusinessException;
import com.cloud.pro.core.utils.IdUtil;
import com.cloud.pro.server.common.config.ServerConfig;
import com.cloud.pro.server.enums.MergeFlagEnum;
import com.cloud.pro.server.modules.context.file.FileChunkSaveContext;
import com.cloud.pro.server.modules.converter.FileConverter;
import com.cloud.pro.server.modules.entity.FileChunk;
import com.cloud.pro.server.modules.mapper.FileChunkMapper;
import com.cloud.pro.server.modules.service.FileChunkService;
import com.cloud.pro.storage.engine.core.StorageEngine;
import com.cloud.pro.storage.engine.core.context.StoreFileChunkContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;

/**
* @author han
* @description 针对表【cloud_pro_file_chunk(文件分片信息表)】的数据库操作Service实现
* @createDate 2024-04-19 15:19:50
*/
@Service
public class FileChunkServiceImpl extends ServiceImpl<FileChunkMapper, FileChunk>
    implements FileChunkService {

    @Resource
    private ServerConfig serverConfig;

    @Resource
    private StorageEngine storageEngine;

    @Resource
    private FileConverter fileConverter;

    /**
     * 文件分片保存
     * @param context
     */
    @Override
    // TODO 线程安全优化：1.使用userId为锁的唯一标识，只有校验部分需要加锁，减小锁的粒度 2.分布式锁，解决单机问题
    public synchronized void saveChunkFile(FileChunkSaveContext context) {
        // 1.上传实体记录
        doStoreFileChunk(context);
        // 2.保存分片文件记录
        doSaveRecord(context);
        // 3.校验是否全部分片上传完成
        doJudgeMergeFile(context);
    }

    /**
     * 委托文件存储引擎上传实体记录
     * @param context
     */
    private void doStoreFileChunk(FileChunkSaveContext context) {
        try {
            StoreFileChunkContext storeFileChunkContext = fileConverter.fileChunkSaveContext2StoreFileChunkContext(context);
            storeFileChunkContext.setInputStream(context.getFile().getInputStream());
            storageEngine.storeChunk(storeFileChunkContext);
            context.setRealPath(storeFileChunkContext.getRealPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("文件分片上传失败");
        }
    }

    /**
     * 保存分片文件记录
     * @param context
     */
    private void doSaveRecord(FileChunkSaveContext context) {
        FileChunk fileChunk = new FileChunk();
        fileChunk.setId(IdUtil.get());
        fileChunk.setIdentifier(context.getIdentifier());
        fileChunk.setRealPath(context.getRealPath());
        fileChunk.setChunkNumber(context.getChunkNumber());
        fileChunk.setExpirationTime(LocalDateTime.now().plusDays(serverConfig.getChunkFileExpirationDays()));
        fileChunk.setCreateUser(context.getUserId());
        if (!this.save(fileChunk)) {
            // 这里保存失败了不用删除存储引擎中已经上传的分片，因为分片默认在存储引擎中只保存一天，过期会自动删除
            throw new BusinessException("文件分片上传失败");
        }
    }

    /**
     * 判断是否所有的分片均上传完成
     * @param context
     */
    private void doJudgeMergeFile(FileChunkSaveContext context) {
        LambdaUpdateWrapper<FileChunk> lqw = new LambdaUpdateWrapper<>();
        lqw.eq(FileChunk::getIdentifier, context.getIdentifier());
        lqw.eq(FileChunk::getCreateUser, context.getUserId());
        long count = this.count(lqw);
        if (count == context.getChunkNumber()) {
            context.setMergeFlagEnum(MergeFlagEnum.READY);
        }
    }
}




