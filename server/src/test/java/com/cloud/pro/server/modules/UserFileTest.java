package com.cloud.pro.server.modules;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.cloud.pro.core.exception.BusinessException;
import com.cloud.pro.core.utils.IdUtil;
import com.cloud.pro.server.enums.DelFlagEnum;
import com.cloud.pro.server.modules.context.file.DeleteFileContext;
import com.cloud.pro.server.modules.context.file.QueryFileListContext;
import com.cloud.pro.server.modules.context.file.SecUploadFileContext;
import com.cloud.pro.server.modules.context.file.UpdateFilenameContext;
import com.cloud.pro.server.modules.context.user.CreateFolderContext;
import com.cloud.pro.server.modules.context.user.UserLoginContext;
import com.cloud.pro.server.modules.context.user.UserRegisterContext;
import com.cloud.pro.server.modules.entity.File;
import com.cloud.pro.server.modules.service.FileService;
import com.cloud.pro.server.modules.service.UserFileService;
import com.cloud.pro.server.modules.service.UserService;
import com.cloud.pro.server.modules.vo.UserFileVO;
import com.cloud.pro.server.modules.vo.UserVO;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 用户文件测试
 */
@SpringBootTest
@Transactional
public class UserFileTest {
    @Resource
    private UserFileService userFileService;

    @Resource
    private UserService userService;

    @Resource
    private FileService fileService;

    /**
     * 测试创建用户文件夹
     */
    @Test
    public void testCreateFolder() {
        CreateFolderContext context = createFolderContext("测试文件.jpg");
        Long fileId = userFileService.createFolder(context);
        Assert.isTrue(fileId > 0);

        // 创建重复文件名的文件
        userFileService.createFolder(context);
        userFileService.createFolder(context);
    }

    @Test
    public void testQueryUserFileList() {
        Long userId = register();
        UserVO userVO = getUserInfo(userId);

        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setParentId(userVO.getRootFileId());
        queryFileListContext.setUserId(userId);
        queryFileListContext.setFileTypeArray(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        queryFileListContext.setDelFlag(DelFlagEnum.NO.getCode());

        List<UserFileVO> fileList = userFileService.getFileList(queryFileListContext);
        Assertions.assertTrue(CollUtil.isEmpty(fileList));
    }

    @Test
    public void testCreateFolderSuccess() {
        Long userId = register();
        UserVO userVO = getUserInfo(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name");

        Long fileId = userFileService.createFolder(context);
        Assertions.assertNotNull(fileId);
    }

    @Test
    public void testUpdateFilenameFail() {
        Long userId = register();
        UserVO userVO = getUserInfo(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name");

        Long fileId = userFileService.createFolder(context);
        Assertions.assertNotNull(fileId);

        // 文件ID无效
        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setFileId(fileId + 1);
        updateFilenameContext.setNewFilename("folder-name-new");
        updateFilenameContext.setUserId(userId);
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            userFileService.updateFilename(updateFilenameContext);
        });
        Assert.isTrue("该文件ID无效".equals(exception.getMessage()));

        // 用户没有权限修改
        updateFilenameContext.setFileId(fileId);
        updateFilenameContext.setUserId(userId + 1);
        exception = Assertions.assertThrows(BusinessException.class, () -> {
            userFileService.updateFilename(updateFilenameContext);
        });
        Assert.isTrue("当前用户没有修改该文件名称的权限".equals(exception.getMessage()));

        // 新旧文件名称不能一样
        updateFilenameContext.setUserId(userId);
        updateFilenameContext.setNewFilename("folder-name");
        exception = Assertions.assertThrows(BusinessException.class, () -> {
            userFileService.updateFilename(updateFilenameContext);
        });
        Assert.isTrue("请设置一个新的文件名称".equals(exception.getMessage()));

        // 不能和当前文件夹下的其他文件名称一样
        updateFilenameContext.setNewFilename("folder-name-new");
        context.setFolderName("folder-name-new");
        Long otherFileId = userFileService.createFolder(context);
        Assertions.assertNotNull(otherFileId);
        exception = Assertions.assertThrows(BusinessException.class, () -> {
            userFileService.updateFilename(updateFilenameContext);
        });
        Assert.isTrue("该名称已被占用".equals(exception.getMessage()));

        // 成功修改文件名称
        updateFilenameContext.setNewFilename("folder-name-new-2");
        userFileService.updateFilename(updateFilenameContext);
    }

    @Test
    public void testDeleteFile() {
        Long userId = register();
        UserVO userVO = getUserInfo(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name");

        Long fileId = userFileService.createFolder(context);
        Assertions.assertNotNull(fileId);

        // 要删除的文件ID不存在
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        deleteFileContext.setFileIdList(Arrays.asList(fileId + 1));
        deleteFileContext.setUserId(userId);
        Exception exception = Assertions.assertThrows(BusinessException.class, () -> {
            userFileService.deleteFile(deleteFileContext);
        });
        Assert.isTrue("要删除的文件不存在".equals(exception.getMessage()));

        // 用户没有删除权限
        deleteFileContext.setFileIdList(Arrays.asList(fileId));
        deleteFileContext.setUserId(userId + 1);
        exception = Assertions.assertThrows(BusinessException.class, () -> {
            userFileService.deleteFile(deleteFileContext);
        });
        Assert.isTrue("当前用户没有删除该文件的权限".equals(exception.getMessage()));

        // 创建子文件
        CreateFolderContext childContext = new CreateFolderContext();
        childContext.setParentId(fileId);
        childContext.setUserId(userId);
        childContext.setFolderName("child-folder");
        Long childFileId = userFileService.createFolder(childContext);
        Assertions.assertNotNull(childFileId);

        // 删除文件成功
        deleteFileContext.setUserId(userId);
        userFileService.deleteFile(deleteFileContext);
    }

    @Test
    public void testSecUpload() {
        Long userId = register();
        UserVO userVO = getUserInfo(userId);

        String identifier = "identifier";

        File file = new File();
        file.setFileId(IdUtil.get());
        file.setFilename("filename");
        file.setRealPath("realpath");
        file.setFileSize("filesize");
        file.setFileSizeDesc("desc");
        file.setFileSuffix(".pdf");
        file.setFilePreviewContentType("");
        file.setIdentifier(identifier);
        file.setCreateUser(userId);
        boolean save = fileService.save(file);
        Assertions.assertTrue(save);

        SecUploadFileContext secUploadFileContext = new SecUploadFileContext();
        secUploadFileContext.setParentId(userVO.getRootFileId());
        secUploadFileContext.setFilename("filename");
        secUploadFileContext.setIdentifier(identifier + "_change");
        secUploadFileContext.setUserId(userId);

        boolean secUploadFail = userFileService.secUpload(secUploadFileContext);
        Assertions.assertFalse(secUploadFail);

        secUploadFileContext.setIdentifier(identifier);
        boolean secUploadSuccess = userFileService.secUpload(secUploadFileContext);
        Assertions.assertTrue(secUploadSuccess);
    }

    /**************************************private**************************************/

    private CreateFolderContext createFolderContext(String filename) {
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(1L);
        context.setUserId(1L);
        context.setFolderName(filename);
        return context;
    }

    private Long register() {
        UserRegisterContext context = createUserRegisterContext();

        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);
        return userId;
    }

    private UserVO getUserInfo(Long userId) {
        UserLoginContext loginContext = getUserLoginContext();

        String accessToken = userService.login(loginContext);

        UserVO userVO = userService.info(userId);
        Assertions.assertNotNull(userVO);
        return userVO;
    }

    private final static String username = "测试用户1";
    private final static String password = "123456";
    /**
     * 创建用户上下文测试实体
     * @return
     */
    @NotNull
    private UserRegisterContext createUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername(username);
        context.setPassword(password);
        return context;
    }

    /**
     * 创建用户登录上下文测试实体
     * @return
     */
    @NotNull
    private UserLoginContext getUserLoginContext() {
        UserLoginContext loginContext = new UserLoginContext();
        loginContext.setUsername(username);
        loginContext.setPassword(password);
        return loginContext;
    }
}
