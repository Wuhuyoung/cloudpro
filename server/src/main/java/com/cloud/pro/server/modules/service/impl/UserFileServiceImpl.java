package com.cloud.pro.server.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.pro.core.constants.CommonConstants;
import com.cloud.pro.core.exception.BusinessException;
import com.cloud.pro.core.utils.IdUtil;
import com.cloud.pro.server.constants.FileConstants;
import com.cloud.pro.server.enums.DelFlagEnum;
import com.cloud.pro.server.enums.FolderFlagEnum;
import com.cloud.pro.server.modules.context.CreateFolderContext;
import com.cloud.pro.server.modules.entity.UserFile;
import com.cloud.pro.server.modules.service.UserFileService;
import com.cloud.pro.server.modules.mapper.UserFileMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


/**
* @author han
* @description 针对表【cloud_pro_user_file(用户文件信息表)】的数据库操作Service实现
* @createDate 2024-02-26 23:00:21
*/
@Service
public class UserFileServiceImpl extends ServiceImpl<UserFileMapper, UserFile> implements UserFileService {

    @Resource
    private UserFileMapper userFileMapper;

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

}




