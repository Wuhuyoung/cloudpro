package com.cloud.pro.server.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

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

    @Resource
    private LockRegistry lockRegistry;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private static final String CHUNK_SAVE_LOCK_KEY_TEMPLATE = "file:lock:chunk:save:%s:%s";

    private static final String CHUNK_MERGE_CACHE_KEY_TEMPLATE = "file:cache:chunk:merge:%s:%s";

    /**
     * 文件分片保存
     * @param context
     */
    @Override
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
        // 分布式锁，保证只有一个线程去查询
        // 先从缓存中查询是否已经有其他线程返回了上传完成
        String mergeSuccess = redisTemplate.opsForValue().get(String.format(CHUNK_MERGE_CACHE_KEY_TEMPLATE, context.getUserId(), context.getIdentifier()));
        if (StringUtils.isNotBlank(mergeSuccess)) {
            return;
        }
        Lock lock = lockRegistry.obtain(String.format(CHUNK_SAVE_LOCK_KEY_TEMPLATE, context.getUserId(), context.getIdentifier()));
        try {
            lock.lock();
            mergeSuccess = redisTemplate.opsForValue().get(String.format(CHUNK_MERGE_CACHE_KEY_TEMPLATE, context.getUserId(), context.getIdentifier()));
            if (StringUtils.isNotBlank(mergeSuccess) && mergeSuccess.equals(String.valueOf(context.getTotalChunks()))) {
                return;
            }
            LambdaQueryWrapper<FileChunk> lqw = new LambdaQueryWrapper<>();
            lqw.eq(FileChunk::getIdentifier, context.getIdentifier());
            lqw.eq(FileChunk::getCreateUser, context.getUserId());
            long count = this.count(lqw);
            if (count == context.getTotalChunks()) {
                context.setMergeFlagEnum(MergeFlagEnum.READY);
                // 保存到缓存中，防止后续其他线程再次返回
                redisTemplate.opsForValue().set(String.format(CHUNK_MERGE_CACHE_KEY_TEMPLATE, context.getUserId(), context.getIdentifier()),
                        String.valueOf(count),
                        30,
                        TimeUnit.SECONDS);
            }
        } finally {
            lock.unlock();
        }
    }
}




