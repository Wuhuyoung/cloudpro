package com.cloud.pro.server.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.pro.core.constants.CommonConstants;
import com.cloud.pro.core.exception.BusinessException;
import com.cloud.pro.core.utils.FileUtil;
import com.cloud.pro.core.utils.IdUtil;
import com.cloud.pro.server.common.event.DeleteFileEvent;
import com.cloud.pro.server.constants.FileConstants;
import com.cloud.pro.server.enums.DelFlagEnum;
import com.cloud.pro.server.enums.FileTypeEnum;
import com.cloud.pro.server.enums.FolderFlagEnum;
import com.cloud.pro.server.modules.context.file.DeleteFileContext;
import com.cloud.pro.server.modules.context.file.FileChunkMergeContext;
import com.cloud.pro.server.modules.context.file.FileChunkSaveContext;
import com.cloud.pro.server.modules.context.file.FileChunkUploadContext;
import com.cloud.pro.server.modules.context.file.FileSaveContext;
import com.cloud.pro.server.modules.context.file.FileUploadContext;
import com.cloud.pro.server.modules.context.file.QueryFileListContext;
import com.cloud.pro.server.modules.context.file.QueryUploadedChunksContext;
import com.cloud.pro.server.modules.context.file.SecUploadFileContext;
import com.cloud.pro.server.modules.context.file.UpdateFilenameContext;
import com.cloud.pro.server.modules.context.user.CreateFolderContext;
import com.cloud.pro.server.modules.converter.FileConverter;
import com.cloud.pro.server.modules.entity.File;
import com.cloud.pro.server.modules.entity.FileChunk;
import com.cloud.pro.server.modules.entity.UserFile;
import com.cloud.pro.server.modules.mapper.UserFileMapper;
import com.cloud.pro.server.modules.service.FileChunkService;
import com.cloud.pro.server.modules.service.FileService;
import com.cloud.pro.server.modules.service.UserFileService;
import com.cloud.pro.server.modules.vo.FileChunkUploadVO;
import com.cloud.pro.server.modules.vo.UploadedChunksVO;
import com.cloud.pro.server.modules.vo.UserFileVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


/**
* @author han
* @description 针对表【cloud_pro_user_file(用户文件信息表)】的数据库操作Service实现
* @createDate 2024-02-26 23:00:21
*/
@Service
public class UserFileServiceImpl extends ServiceImpl<UserFileMapper, UserFile>
        implements UserFileService, ApplicationContextAware {

    @Resource
    private FileService fileService;

    @Resource
    private FileChunkService fileChunkService;

    @Resource
    private UserFileMapper userFileMapper;

    @Resource
    private FileConverter fileConverter;

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        UserFileServiceImpl.applicationContext = applicationContext;
    }

    /**
     * 创建文件夹信息
     * @param createFolderContext
     * @return 文件id
     */
    @Override
    public Long createFolder(CreateFolderContext createFolderContext) {
        return saveUserFile(createFolderContext.getParentId(),
                createFolderContext.getFolderName(),
                FolderFlagEnum.YES,
                null,
                null,
                createFolderContext.getUserId(),
                null);
    }

    /**
     * 查询用户的根文件夹信息
     * @param userId
     * @return
     */
    @Override
    public UserFile getUserRootFile(Long userId) {
        LambdaQueryWrapper<UserFile> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserFile::getUserId, userId);
        lqw.eq(UserFile::getParentId, FileConstants.TOP_PARENT_ID);
        lqw.eq(UserFile::getDelFlag, DelFlagEnum.NO.getCode());
        lqw.eq(UserFile::getFolderFlag, FolderFlagEnum.YES.getCode());
        return userFileMapper.selectOne(lqw);
    }

    /**
     * 查询用户文件列表
     * @param queryFileListContext
     * @return
     */
    @Override
    public List<UserFileVO> getFileList(QueryFileListContext queryFileListContext) {
        List<UserFileVO> userFileVOList = userFileMapper.selectFileList(queryFileListContext);
        return userFileVOList;
    }

    /**
     * 更新文件名称
     * @param context
     */
    @Override
    public void updateFilename(UpdateFilenameContext context) {
        checkUpdateFilenameCondition(context);
        UserFile userFile = context.getUserFile();
        userFile.setFilename(context.getNewFilename());
        userFile.setUpdateUser(context.getUserId());
        boolean result = this.updateById(userFile);
        if (!result) {
            throw new BusinessException("文件重命名失败");
        }
    }

    /**
     * 批量删除文件
     * @param context
     */
    @Override
    public void deleteFile(DeleteFileContext context) {
        // 1.校验删除的条件
        checkFileDeleteCondition(context);
        // 2.执行批量删除
        doDeleteFile(context);
    }

    /**
     * 文件秒传
     * @param context
     * @return
     */
    @Override
    public boolean secUpload(SecUploadFileContext context) {
        // 1.通过文件的唯一标识，查找对应的实体文件记录
        File file = getFileByIdentifier(context.getIdentifier());
        // 2.如果没有查到，直接返回秒传失败
        if (Objects.isNull(file)) {
            return false;
        }
        // 3.如果查到，直接挂载关联关系，返回秒传成功
        saveUserFile(context.getParentId(),
                context.getFilename(),
                FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtil.getFileSuffix(context.getFilename())),
                file.getFileId(), // realFileId
                context.getUserId(),
                file.getFileSizeDesc());
        return true;
    }

    /**
     * 单文件上传
     * @param context
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upload(FileUploadContext context) {
        // 1.上传文件并保存实体文件的记录
        FileSaveContext fileSaveContext = fileConverter.fileUploadContext2FileSaveContext(context);
        fileService.saveFile(fileSaveContext);
        File record = fileSaveContext.getRecord();
        // 2.保存用户文件的关系记录
        saveUserFile(context.getParentId(),
                fileSaveContext.getFilename(),
                FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtil.getFileSuffix(context.getFilename())),
                record.getFileId(),
                context.getUserId(),
                record.getFileSizeDesc());
    }

    /**
     * 分片上传
     * @param context
     * @return
     */
    @Override
    public FileChunkUploadVO chunkUpload(FileChunkUploadContext context) {
        // 以下逻辑由FileChunkService实现
        // 1.上传实体记录
        // 2.保存分片文件记录
        // 3.校验是否全部分片上传完成
        FileChunkSaveContext fileChunkSaveContext = fileConverter.fileChunkUploadContext2FileChunkSaveContext(context);
        fileChunkService.saveChunkFile(fileChunkSaveContext);
        FileChunkUploadVO vo = new FileChunkUploadVO();
        vo.setMergeFlag(fileChunkSaveContext.getMergeFlagEnum().getCode());
        return vo;
    }

    /**
     * 查询已上传的文件分片列表
     * @param context
     * @return
     */
    @Override
    public UploadedChunksVO getUploadedChunks(QueryUploadedChunksContext context) {
        LambdaQueryWrapper<FileChunk> lqw = new LambdaQueryWrapper<>();
        // 只查询其中chunk_number这个字段
        lqw.select(FileChunk::getChunkNumber);
        lqw.eq(FileChunk::getIdentifier, context.getIdentifier());
        lqw.eq(FileChunk::getCreateUser, context.getUserId());
        lqw.gt(FileChunk::getExpirationTime, LocalDateTime.now());

        // listObjs()适用于只需要查询一个字段，listMaps()适用于需要查询其中的多个字段
        List<Integer> uploadedChunks = fileChunkService.listObjs(lqw, value -> Integer.parseInt(String.valueOf(value)));

        UploadedChunksVO vo = new UploadedChunksVO();
        vo.setUploadedChunks(uploadedChunks);
        return vo;
    }

    /**
     * 文件分片合并
     * @param context
     */
    @Override
    @Transactional
    public void mergeFile(FileChunkMergeContext context) {
        // 1.合并文件分片，保存物理文件记录
        fileService.mergeFileChunkAndSaveFile(context);

        // 2.保存用户文件关系映射
        saveUserFile(context.getParentId(),
                context.getFilename(),
                FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtil.getFileSuffix(context.getFilename())),
                context.getRecord().getFileId(),
                context.getUserId(),
                context.getRecord().getFileSizeDesc());
    }

    /**********************************private**********************************/

    /**
     * 保存用户文件的映射记录
     * @param parentId
     * @param filename
     * @param folderFlagEnum
     * @param fileType
     * @param realFileId
     * @param userId
     * @param fileSizeDesc
     * @return 文件id
     */
    private Long saveUserFile(Long parentId,
                              String filename,
                              FolderFlagEnum folderFlagEnum,
                              Integer fileType,
                              Long realFileId,
                              Long userId,
                              String fileSizeDesc) {
        UserFile entity = assembleUserFile(parentId, filename, folderFlagEnum, fileType, realFileId, userId, fileSizeDesc);
        boolean saveResult = save(entity);
        if (!saveResult) {
            throw new BusinessException("保存文件信息失败");
        }
        return entity.getFileId();
    }

    private UserFile assembleUserFile(Long parentId, String filename, FolderFlagEnum folderFlagEnum, Integer fileType, Long realFileId, Long userId, String fileSizeDesc) {
        // 1.构建并填充实体信息
        UserFile userFile = new UserFile();

        userFile.setFileId(IdUtil.get());
        userFile.setUserId(userId);
        userFile.setParentId(parentId);
        userFile.setRealFileId(realFileId);
        userFile.setFilename(filename);
        userFile.setFolderFlag(folderFlagEnum.getCode());
        userFile.setFileSizeDesc(fileSizeDesc);
        userFile.setFileType(fileType);
        userFile.setDelFlag(DelFlagEnum.NO.getCode());
        userFile.setCreateUser(userId);
        userFile.setUpdateUser(userId);

        // 2.解决同一个文件夹下文件重名的问题
        handleDuplicateFilename(userFile);

        return userFile;
    }

    /**
     * 同一文件夹下相同文件名，需要按照系统级规则重命名
     * @param userFile
     */
    private void handleDuplicateFilename(UserFile userFile) {
        String filename = userFile.getFilename();
        String newFilenameWithoutSuffix, newFilenameSuffix;
        int pointIndex = filename.lastIndexOf(CommonConstants.POINT_STR);
        // 没有文件后缀
        if (pointIndex == CommonConstants.MINUS_ONE_INT) {
            newFilenameWithoutSuffix = filename;
            newFilenameSuffix = StringUtils.EMPTY;
        } else { // 有文件后缀，123.jpg
            newFilenameWithoutSuffix = filename.substring(CommonConstants.ZERO_INT, pointIndex); // 123
            newFilenameSuffix = filename.replace(newFilenameWithoutSuffix, StringUtils.EMPTY); // .jpg
        }

        // 查找重复名称的文件
        List<UserFile> fileList = getDuplicateFilename(userFile, newFilenameWithoutSuffix, newFilenameSuffix);
        if (CollectionUtils.isEmpty(fileList)) {
            return;
        }

        List<String> filenameList = fileList.stream().map(UserFile::getFilename).collect(Collectors.toList());

        int count = 1;
        while (filenameList.contains(filename)) {
            filename = assembleNewFilename(newFilenameWithoutSuffix, count, newFilenameSuffix);
            count++;
        }

        userFile.setFilename(filename);
    }

    /**
     * 查找同一文件夹下同名的文件数
     * @param userFile
     * @param newFilenameWithoutSuffix 123
     * @param newFilenameSuffix .jpg
     * @return
     */
    private List<UserFile> getDuplicateFilename(UserFile userFile, String newFilenameWithoutSuffix, String newFilenameSuffix) {
        LambdaQueryWrapper<UserFile> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserFile::getUserId, userFile.getUserId());
        lqw.eq(UserFile::getDelFlag, DelFlagEnum.NO.getCode());
        lqw.eq(UserFile::getParentId, userFile.getParentId());
        lqw.eq(UserFile::getFolderFlag, userFile.getFolderFlag());
        lqw.likeRight(UserFile::getFilename, newFilenameWithoutSuffix);
        return list(lqw);
    }

    /**
     * 拼装新文件名称
     * 拼装规则参考操作系统重复文件名称的重命名规范
     * @param newFilenameWithoutSuffix
     * @param count
     * @param newFilenameSuffix
     * @return
     */
    private String assembleNewFilename(String newFilenameWithoutSuffix, int count, String newFilenameSuffix) {
        StringBuilder newFilename = new StringBuilder(newFilenameWithoutSuffix)
                .append(FileConstants.CN_LEFT_PARENTHESES_STR)
                .append(count)
                .append(FileConstants.CN_RIGHT_PARENTHESES_STR)
                .append(newFilenameSuffix);
        return newFilename.toString();
    }

    /**
     * 更新文件名称的条件校验
     * @param context
     */
    private void checkUpdateFilenameCondition(UpdateFilenameContext context) {
        // 1.文件ID是有效的
        Long fileId = context.getFileId();
        UserFile userFile = this.getById(fileId);
        if (Objects.isNull(userFile)) {
            throw new BusinessException("该文件ID无效");
        }
        // 2.用户有权限更新该文件的名称
        if (!Objects.equals(userFile.getUserId(), context.getUserId())) {
            throw new BusinessException("当前用户没有修改该文件名称的权限");
        }
        // 3.新旧文件名称不能一样
        if (StringUtils.equals(userFile.getFilename(), context.getNewFilename())) {
            throw new BusinessException("请设置一个新的文件名称");
        }
        // 4.不能和当前文件夹下的其他文件名称一样
        LambdaQueryWrapper<UserFile> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserFile::getParentId, userFile.getParentId());
        lqw.eq(UserFile::getFilename, context.getNewFilename());
        lqw.eq(UserFile::getUserId, context.getUserId());
        lqw.eq(UserFile::getFolderFlag, userFile.getFolderFlag());
        lqw.eq(UserFile::getDelFlag, DelFlagEnum.NO.getCode());

        long count = this.count(lqw);
        if (count > 0) {
            throw new BusinessException("该名称已被占用");
        }
        context.setUserFile(userFile);
    }

    /**
     * 校验文件删除的条件
     * @param deleteFileContext
     */
    private void checkFileDeleteCondition(DeleteFileContext deleteFileContext) {
        List<Long> fileIdList = deleteFileContext.getFileIdList();
        Long userId = deleteFileContext.getUserId();

        // 1.文件ID合法
        List<UserFile> userFileList = this.listByIds(fileIdList);
        if (CollectionUtils.isEmpty(userFileList)) {
            throw new BusinessException("要删除的文件不存在");
        }
        if (userFileList.size() != fileIdList.size()) {
            throw new BusinessException("存在不合法的文件记录");
        }
        // 2.用户拥有删除该文件的权限
        Set<Long> userIdSet = userFileList.stream().map(UserFile::getUserId).collect(Collectors.toSet());
        if (userIdSet.size() != 1) {
            throw new BusinessException("存在不合法的文件记录");
        }
        Long dbUserId = userIdSet.stream().findFirst().get();
        if (!Objects.equals(dbUserId, userId)) {
            throw new BusinessException("当前用户没有删除该文件的权限");
        }
    }

    /**
     * 执行文件删除的逻辑
     * 1.需递归删除文件夹下的文件
     * 2.对外发布批量删除文件的事件，给其他模块订阅使用
     * @param deleteFileContext
     */
    private void doDeleteFile(DeleteFileContext deleteFileContext) {
        List<Long> fileIdList = deleteFileContext.getFileIdList();

        // 查询文件夹下的子文件
        DeleteFileContext childrenFileContext = getChildrenFile(deleteFileContext);
        if (!Objects.isNull(childrenFileContext)) {
            // 递归删除文件夹下的子文件
            doDeleteFile(childrenFileContext);
        }

        // 删除当前文件
        LambdaUpdateWrapper<UserFile> luw = new LambdaUpdateWrapper<>();
        luw.in(UserFile::getFileId, fileIdList);
        luw.set(UserFile::getDelFlag, DelFlagEnum.YES.getCode());
        if (!this.update(luw)) {
            throw new BusinessException("文件删除失败");
        }
        // 发布删除文件的事件
        DeleteFileEvent deleteFileEvent = new DeleteFileEvent(this, fileIdList);
        applicationContext.publishEvent(deleteFileEvent);
    }

    /**
     * 获取当前文件夹下的所有子文件
     * @param deleteFileContext
     * @return
     */
    private DeleteFileContext getChildrenFile(DeleteFileContext deleteFileContext) {
        List<Long> fileIdList = deleteFileContext.getFileIdList();
        Long userId = deleteFileContext.getUserId();

        LambdaUpdateWrapper<UserFile> lqw = new LambdaUpdateWrapper<>();
        lqw.in(UserFile::getParentId, fileIdList);
        lqw.eq(UserFile::getDelFlag, DelFlagEnum.NO.getCode());
        List<UserFile> userFileList = this.list(lqw);
        if (CollectionUtils.isEmpty(userFileList)) {
            return null;
        }
        List<Long> childrenFileIdList = userFileList.stream().map(UserFile::getFileId).collect(Collectors.toList());
        DeleteFileContext childrenContext = new DeleteFileContext();
        childrenContext.setFileIdList(childrenFileIdList);
        childrenContext.setUserId(userId);
        return childrenContext;
    }

    /**
     * 通过文件的唯一标识查找文件
     * @param identifier
     * @return
     */
    private File getFileByIdentifier(String identifier) {
        LambdaUpdateWrapper<File> lqw = new LambdaUpdateWrapper<>();
        lqw.eq(File::getIdentifier, identifier);
        //todo 这里可能有其他用户在并发查询，然后都没有查到唯一标识，最后会插入两条具有相同identifier的物理文件，秒传业务需要加锁保证线程安全
        // 方法一：查询时加锁，如果没有查到identifier就先向数据库中添加一个identifier？这里逻辑有些问题，性能不高
        // 方法二：可以用Redis先存储一下identifier：先查Redis(需加锁保证线程安全)，有相同的identifier就实现秒传，没有就保存到Redis中，释放锁，再去数据库中查(此时就不用加锁了)
        //  数据库中有就可以秒传，没有就不能
        // 这里的业务逻辑考虑一下如何保证 性能和线程安全

        return fileService.getOne(lqw);
    }

}

