package com.cloud.pro.server.modules;

import cn.hutool.core.lang.Assert;
import com.cloud.pro.server.modules.context.CreateFolderContext;
import com.cloud.pro.server.modules.service.UserFileService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 用户文件测试
 */
@SpringBootTest
@Transactional
public class UserFileTest {
    @Resource
    private UserFileService userFileService;

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

    private CreateFolderContext createFolderContext(String filename) {
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(1L);
        context.setUserId(1L);
        context.setFolderName(filename);
        return context;
    }
}
