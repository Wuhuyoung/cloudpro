package com.cloud.pro.server.modules;

import cn.hutool.core.lang.Assert;
import com.cloud.pro.core.exception.BusinessException;
import com.cloud.pro.server.enums.ShareDayTypeEnum;
import com.cloud.pro.server.enums.ShareTypeEnum;
import com.cloud.pro.server.modules.context.share.CancelShareContext;
import com.cloud.pro.server.modules.context.share.CheckShareCodeContext;
import com.cloud.pro.server.modules.context.share.CreateShareUrlContext;
import com.cloud.pro.server.modules.context.share.QueryChildFileListContext;
import com.cloud.pro.server.modules.context.share.QueryShareDetailContext;
import com.cloud.pro.server.modules.context.share.QueryShareListContext;
import com.cloud.pro.server.modules.context.share.QueryShareSimpleDetailContext;
import com.cloud.pro.server.modules.context.user.CreateFolderContext;
import com.cloud.pro.server.modules.context.user.UserLoginContext;
import com.cloud.pro.server.modules.context.user.UserRegisterContext;
import com.cloud.pro.server.modules.service.ShareService;
import com.cloud.pro.server.modules.service.UserFileService;
import com.cloud.pro.server.modules.service.UserService;
import com.cloud.pro.server.modules.vo.ShareDetailVO;
import com.cloud.pro.server.modules.vo.ShareSimpleDetailVO;
import com.cloud.pro.server.modules.vo.ShareUrlListVO;
import com.cloud.pro.server.modules.vo.ShareUrlVO;
import com.cloud.pro.server.modules.vo.UserFileVO;
import com.cloud.pro.server.modules.vo.UserVO;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分享模块测试
 */
@SpringBootTest
@Transactional
public class ShareTest {
    @Resource
    private UserFileService userFileService;

    @Resource
    private UserService userService;

    @Resource
    private ShareService shareService;

    @Test
    public void testCreateShare() {
        Long userId = register();
        UserVO userVO = getUserInfo(userId);

        // 创建文件夹 1
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name");
        Long fileId = userFileService.createFolder(context);
        Assertions.assertNotNull(fileId);

        // 创建分享链接
        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("share-1");
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.THIRTY_DAYS_VALIDITY.getCode());
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(fileId));
        createShareUrlContext.setUserId(userId);
        ShareUrlVO shareUrlVO = shareService.create(createShareUrlContext);
        Assertions.assertNotNull(shareUrlVO);
    }

    @Test
    public void testQueryShareList() {
        Long userId = register();
        UserVO userVO = getUserInfo(userId);

        // 创建文件夹 1
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name");
        Long fileId = userFileService.createFolder(context);
        Assertions.assertNotNull(fileId);

        // 创建分享链接
        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("share-1");
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.THIRTY_DAYS_VALIDITY.getCode());
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(fileId));
        createShareUrlContext.setUserId(userId);
        ShareUrlVO shareUrlVO = shareService.create(createShareUrlContext);
        Assertions.assertNotNull(shareUrlVO);

        // 查询分享列表
        QueryShareListContext queryShareListContext = new QueryShareListContext();
        queryShareListContext.setUserId(userId);
        List<ShareUrlListVO> result = shareService.getShares(queryShareListContext);
        Assertions.assertTrue(CollectionUtils.isNotEmpty(result));
    }

    @Test
    public void testCancelShare() {
        Long userId = register();
        UserVO userVO = getUserInfo(userId);

        // 创建文件夹 1
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name");
        Long fileId = userFileService.createFolder(context);
        Assertions.assertNotNull(fileId);

        // 创建分享链接
        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("share-1");
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.THIRTY_DAYS_VALIDITY.getCode());
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(fileId));
        createShareUrlContext.setUserId(userId);
        ShareUrlVO shareUrlVO = shareService.create(createShareUrlContext);
        Assertions.assertNotNull(shareUrlVO);

        // 查询分享列表
        QueryShareListContext queryShareListContext = new QueryShareListContext();
        queryShareListContext.setUserId(userId);
        List<ShareUrlListVO> result = shareService.getShares(queryShareListContext);
        Assertions.assertTrue(CollectionUtils.isNotEmpty(result));

        // 取消分享
        CancelShareContext cancelShareContext = new CancelShareContext();
        cancelShareContext.setShareIdList(Lists.newArrayList(shareUrlVO.getShareId()));
        cancelShareContext.setUserId(userId);
        shareService.cancelShare(cancelShareContext);

        // 查询分享列表
        result = shareService.getShares(queryShareListContext);
        Assertions.assertTrue(CollectionUtils.isEmpty(result));
    }

    @Test
    public void testCheckShareCode() {
        Long userId = register();
        UserVO userVO = getUserInfo(userId);

        // 创建文件夹 1
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name");
        Long fileId = userFileService.createFolder(context);
        Assertions.assertNotNull(fileId);

        // 创建分享链接
        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("share-1");
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.THIRTY_DAYS_VALIDITY.getCode());
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(fileId));
        createShareUrlContext.setUserId(userId);
        ShareUrlVO vo = shareService.create(createShareUrlContext);
        Assertions.assertNotNull(vo);

        // 校验分享码
        CheckShareCodeContext checkShareCodeContext = new CheckShareCodeContext();
        checkShareCodeContext.setShareId(vo.getShareId());
        checkShareCodeContext.setShareCode(vo.getShareCode() + "_change");
        Exception exception = Assertions.assertThrows(BusinessException.class, () -> {
            shareService.checkShareCode(checkShareCodeContext);
        });
        Assert.isTrue("分享码错误".equals(exception.getMessage()));

        checkShareCodeContext.setShareCode(vo.getShareCode());
        String token = shareService.checkShareCode(checkShareCodeContext);
        Assertions.assertTrue(StringUtils.isNotBlank(token));
    }

    @Test
    public void testQueryShareDetail() {
        Long userId = register();
        UserVO userVO = getUserInfo(userId);

        // 创建文件夹 1
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name");
        Long fileId = userFileService.createFolder(context);
        Assertions.assertNotNull(fileId);

        // 创建分享链接
        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("share-1");
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.THIRTY_DAYS_VALIDITY.getCode());
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(fileId));
        createShareUrlContext.setUserId(userId);
        ShareUrlVO vo = shareService.create(createShareUrlContext);
        Assertions.assertNotNull(vo);

        // 查询分享详情
        QueryShareDetailContext queryShareDetailContext = new QueryShareDetailContext();
        queryShareDetailContext.setShareId(vo.getShareId());
        ShareDetailVO shareDetailVO = shareService.detail(queryShareDetailContext);
        Assertions.assertNotNull(shareDetailVO);
    }

    @Test
    public void testQuerySimpleShareDetail() {
        Long userId = register();
        UserVO userVO = getUserInfo(userId);

        // 创建文件夹 1
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name");
        Long fileId = userFileService.createFolder(context);
        Assertions.assertNotNull(fileId);

        // 创建分享链接
        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("share-1");
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.THIRTY_DAYS_VALIDITY.getCode());
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(fileId));
        createShareUrlContext.setUserId(userId);
        ShareUrlVO vo = shareService.create(createShareUrlContext);
        Assertions.assertNotNull(vo);

        // 查询简单的分享详情
        QueryShareSimpleDetailContext queryShareSimpleDetailContext = new QueryShareSimpleDetailContext();
        queryShareSimpleDetailContext.setShareId(vo.getShareId());
        ShareSimpleDetailVO simpleDetailVO = shareService.simpleDetail(queryShareSimpleDetailContext);
        Assertions.assertNotNull(simpleDetailVO);
    }

    @Test
    public void testQueryShareFileList() {
        Long userId = register();
        UserVO userVO = getUserInfo(userId);

        // 创建文件夹 1
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-1");
        Long fileId1 = userFileService.createFolder(context);
        Assertions.assertNotNull(fileId1);

        // 创建文件夹 1/2
        context.setParentId(fileId1);
        context.setFolderName("folder-name-2");
        Long fileId2 = userFileService.createFolder(context);
        Assertions.assertNotNull(fileId2);

        // 创建分享链接
        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("share-1");
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.THIRTY_DAYS_VALIDITY.getCode());
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(userVO.getRootFileId()));
        createShareUrlContext.setUserId(userId);
        ShareUrlVO vo = shareService.create(createShareUrlContext);
        Assertions.assertNotNull(vo);

        // 查询下一级文件列表
        QueryChildFileListContext queryChildFileListContext = new QueryChildFileListContext();
        queryChildFileListContext.setShareId(vo.getShareId());
        queryChildFileListContext.setParentId(fileId1);
        List<UserFileVO> result = shareService.fileList(queryChildFileListContext);
        Assertions.assertTrue(CollectionUtils.isNotEmpty(result));
    }

    @Test
    public void initData() {
        CreateShareUrlContext context = new CreateShareUrlContext();
        context.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        context.setShareDayType(ShareDayTypeEnum.PERMANENT_VALIDITY.getCode());
        context.setShareFileIdList(Lists.newArrayList(1783498376470192128L));
        context.setUserId(1783497259895525378L);
        // 插入500万条分享数据
        for (int i = 0; i < 5000000; i++) {
            context.setShareName("测试分享" + i);
            shareService.create(context);
        }
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
