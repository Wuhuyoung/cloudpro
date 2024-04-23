package com.cloud.pro.server.modules;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.pro.core.exception.BusinessException;
import com.cloud.pro.server.modules.context.file.DeleteFileContext;
import com.cloud.pro.server.modules.context.file.QueryBreadcrumbsContext;
import com.cloud.pro.server.modules.context.file.QueryFolderTreeContext;
import com.cloud.pro.server.modules.context.recycle.DeleteContext;
import com.cloud.pro.server.modules.context.recycle.QueryRecycleFileListContext;
import com.cloud.pro.server.modules.context.recycle.RestoreContext;
import com.cloud.pro.server.modules.context.user.CreateFolderContext;
import com.cloud.pro.server.modules.context.user.UserLoginContext;
import com.cloud.pro.server.modules.context.user.UserRegisterContext;
import com.cloud.pro.server.modules.entity.UserFile;
import com.cloud.pro.server.modules.service.RecycleService;
import com.cloud.pro.server.modules.service.UserFileService;
import com.cloud.pro.server.modules.service.UserService;
import com.cloud.pro.server.modules.vo.BreadcrumbVO;
import com.cloud.pro.server.modules.vo.FolderTreeNodeVO;
import com.cloud.pro.server.modules.vo.UserFileVO;
import com.cloud.pro.server.modules.vo.UserVO;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 回收站模块测试
 */
@SpringBootTest
@Transactional
public class RecycleTest {

    @Resource
    private UserService userService;

    @Resource
    private UserFileService userFileService;

    @Resource
    private RecycleService recycleService;

    @Test
    public void testQueryRecycleFileList() {
        Long userId = register();
        UserVO userVO = getUserInfo(userId);

        // 创建文件夹 1/2
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-1");
        Long fileId1 = userFileService.createFolder(context);
        Assertions.assertNotNull(fileId1);
        context.setFolderName("folder-name-2");
        context.setParentId(fileId1);
        Long fileId2 = userFileService.createFolder(context);
        Assertions.assertNotNull(fileId2);

        // 删除文件夹1
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        deleteFileContext.setFileIdList(Lists.newArrayList(fileId1));
        deleteFileContext.setUserId(userId);
        userFileService.deleteFile(deleteFileContext);

        // 查询回收站列表
        QueryRecycleFileListContext queryRecycleFileListContext = new QueryRecycleFileListContext();
        queryRecycleFileListContext.setUserId(userId);
        List<UserFileVO> recycleFileList = recycleService.recycles(queryRecycleFileListContext);
        Assertions.assertTrue(CollectionUtil.isNotEmpty(recycleFileList));
        Assertions.assertEquals(recycleFileList.size(), 2);
    }

    @Test
    public void testFileRestoreSuccess() {
        Long userId = register();
        UserVO userVO = getUserInfo(userId);

        // 创建文件夹 1
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-1");
        Long fileId1 = userFileService.createFolder(context);
        Assertions.assertNotNull(fileId1);

        // 删除文件夹1
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        deleteFileContext.setFileIdList(Lists.newArrayList(fileId1));
        deleteFileContext.setUserId(userId);
        userFileService.deleteFile(deleteFileContext);

        // 还原文件夹1
        RestoreContext restoreContext = new RestoreContext();
        restoreContext.setFileIdList(Lists.newArrayList(fileId1));
        restoreContext.setUserId(userId);
        recycleService.restore(restoreContext);

        // 查询文件夹树查看结果
        QueryFolderTreeContext queryFolderTreeContext = new QueryFolderTreeContext();
        queryFolderTreeContext.setUserId(userId);
        List<FolderTreeNodeVO> folderTree = userFileService.getFolderTree(queryFolderTreeContext);
        Assertions.assertEquals(folderTree.size(), 1);
//        folderTree.forEach(FolderTreeNodeVO::print);
    }

    @Test
    public void testFileRestoreFailByWrongId() {
        Long userId = register();
        UserVO userVO = getUserInfo(userId);

        // 创建文件夹 1
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-1");
        Long fileId1 = userFileService.createFolder(context);
        Assertions.assertNotNull(fileId1);

        // 删除文件夹1
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        deleteFileContext.setFileIdList(Lists.newArrayList(fileId1));
        deleteFileContext.setUserId(userId);
        userFileService.deleteFile(deleteFileContext);

        // 还原文件夹1
        RestoreContext restoreContext = new RestoreContext();
        restoreContext.setFileIdList(Lists.newArrayList(fileId1));
        restoreContext.setUserId(userId + 1);
        Exception exception = Assertions.assertThrows(BusinessException.class, () -> {
            recycleService.restore(restoreContext);
        });
        Assert.isTrue("您无权执行文件还原".equals(exception.getMessage()));
    }

    @Test
    public void testFileRestoreFailBySameFilename() {
        Long userId = register();
        UserVO userVO = getUserInfo(userId);

        // 创建文件夹 1
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-1");
        Long fileId1 = userFileService.createFolder(context);
        Assertions.assertNotNull(fileId1);

        // 删除文件夹1
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        deleteFileContext.setFileIdList(Lists.newArrayList(fileId1));
        deleteFileContext.setUserId(userId);
        userFileService.deleteFile(deleteFileContext);

        // 再创建文件夹1
        Long fileId11 = userFileService.createFolder(context);
        Assertions.assertNotNull(fileId11);

        // 还原文件夹1
        RestoreContext restoreContext = new RestoreContext();
        restoreContext.setFileIdList(Lists.newArrayList(fileId1));
        restoreContext.setUserId(userId);
        Exception exception = Assertions.assertThrows(BusinessException.class, () -> {
            recycleService.restore(restoreContext);
        });

        // 删除文件夹1
        deleteFileContext.setFileIdList(Lists.newArrayList(fileId11));
        userFileService.deleteFile(deleteFileContext);

        // 还原文件夹1
        restoreContext.setFileIdList(Lists.newArrayList(fileId1, fileId11));
        exception = Assertions.assertThrows(BusinessException.class, () -> {
            recycleService.restore(restoreContext);
        });
        Assert.isTrue("还原失败，存在同名文件，请逐个还原并重命名".equals(exception.getMessage()));
    }

    @Test
    public void testDeleteFile() {
        Long userId = register();
        UserVO userVO = getUserInfo(userId);

        // 创建文件夹 1
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-1");
        Long fileId1 = userFileService.createFolder(context);
        Assertions.assertNotNull(fileId1);

        // 删除文件夹1
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        deleteFileContext.setFileIdList(Lists.newArrayList(fileId1));
        deleteFileContext.setUserId(userId);
        userFileService.deleteFile(deleteFileContext);

        // 彻底删除文件夹1
        DeleteContext deleteContext = new DeleteContext();
        deleteContext.setFileIdList(Lists.newArrayList(fileId1));
        deleteContext.setUserId(userId + 1);
        Exception exception = Assertions.assertThrows(BusinessException.class, () -> {
            recycleService.delete(deleteContext);
        });
        Assert.isTrue("您无权执行文件删除".equals(exception.getMessage()));

        deleteContext.setUserId(userId);
        recycleService.delete(deleteContext);
        // 文件查询
        LambdaQueryWrapper<UserFile> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserFile::getUserId, userId);
        lqw.eq(UserFile::getFileId, fileId1);
        List<UserFile> userFileList = userFileService.list(lqw);
        Assertions.assertTrue(CollectionUtils.isEmpty(userFileList));
    }

    /**************************************private**************************************/

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
