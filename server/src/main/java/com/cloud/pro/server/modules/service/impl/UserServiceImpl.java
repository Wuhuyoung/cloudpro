package com.cloud.pro.server.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.pro.server.constants.FileConstants;
import com.cloud.pro.server.constants.UserConstants;
import com.cloud.pro.core.exception.BusinessException;
import com.cloud.pro.core.response.ResponseCode;
import com.cloud.pro.core.utils.JwtUtil;
import com.cloud.pro.core.utils.PasswordUtil;
import com.cloud.pro.server.modules.context.CreateFolderContext;
import com.cloud.pro.server.modules.context.UserLoginContext;
import com.cloud.pro.server.modules.entity.User;
import com.cloud.pro.server.modules.mapper.UserMapper;
import com.cloud.pro.server.modules.service.UserService;
import com.cloud.pro.server.modules.context.UserRegisterContext;
import com.cloud.pro.server.modules.converter.UserConverter;
import com.cloud.pro.server.modules.service.UserFileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
* @author han
* @description 针对表【cloud_pro_user(用户信息表)】的数据库操作Service实现
* @createDate 2024-02-23 18:28:45
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserConverter userConverter;

    @Resource
    private UserFileService userFileService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户注册
     * @param userRegisterContext
     * @return userId
     */
    @Override
    public Long register(UserRegisterContext userRegisterContext) {
        assembleUserEntity(userRegisterContext);
        // 创建新用户，保存用户信息
        doRegister(userRegisterContext);
        // 创建用户的根目录信息
        createUserRootFolder(userRegisterContext);
        return userRegisterContext.getEntity().getUserId();
    }

    /**
     * 用户登录
     * @param userLoginContext
     * @return accessToken
     */
    @Override
    public String login(UserLoginContext userLoginContext) {
        // 校验登录信息
        checkLoginInfo(userLoginContext);
        // 生成一个有时效性的accessToken，缓存token，实现单设备登录限制(用户只能在一个设备登录)
        generateAndSaveAccessToken(userLoginContext);
        return userLoginContext.getAccessToken();
    }

    /**
     * 退出登录
     * @param userId
     */
    @Override
    public void exit(Long userId) {
        // 清除accessToken
        try {
            redisTemplate.delete(UserConstants.USER_LOGIN_TOKEN_PREFIX + userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("用户退出登录失败");
        }
    }


    /**********************************private**********************************/

    /**
     * 创建用户的根目录信息
     * @param userRegisterContext
     */
    private void createUserRootFolder(UserRegisterContext userRegisterContext) {
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(FileConstants.TOP_PARENT_ID);
        createFolderContext.setUserId(userRegisterContext.getEntity().getUserId());
        createFolderContext.setFolderName(FileConstants.ALL_FILE_CN_STR);
        userFileService.createFolder(createFolderContext);
    }

    /**
     * 注册，创建新用户
     * @param userRegisterContext
     */
    private void doRegister(UserRegisterContext userRegisterContext) {
        User user = userRegisterContext.getEntity();
        if (Objects.isNull(user)) {
            throw new BusinessException(ResponseCode.ERROR);
        }
        try {
            boolean save = save(user);
            if (!save) {
                throw new BusinessException("用户注册失败");
            }
        } catch (DuplicateKeyException e) {
            throw new BusinessException("用户名已存在");
        }
    }

    /**
     * 将上下文信息转化为用户实体
     * @param userRegisterContext
     */
    private void assembleUserEntity(UserRegisterContext userRegisterContext) {
        User user = userConverter.registerContext2User(userRegisterContext);
        String salt = PasswordUtil.getSalt();
        String dbPassword = PasswordUtil.encryptPassword(salt, userRegisterContext.getPassword());
        user.setSalt(salt);
        user.setPassword(dbPassword);
        userRegisterContext.setEntity(user);
    }

    /**
     * 校验用户登录信息
     * @param userLoginContext
     */
    private void checkLoginInfo(UserLoginContext userLoginContext) {
        String username = userLoginContext.getUsername();
        String password = userLoginContext.getPassword();
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUsername, username);
        User user = userMapper.selectOne(lqw);
        if (Objects.isNull(user)) {
            throw new BusinessException("用户名不存在");
        }
        String dbPassword = user.getPassword();
        String salt = user.getSalt();

        if (!StringUtils.equals(dbPassword, PasswordUtil.encryptPassword(salt, password))) {
            throw new BusinessException("用户名与密码不匹配");
        }

        userLoginContext.setEntity(user);
    }

    /**
     * 生成并保存登录的accessToken，实现单设备登录限制
     * @param userLoginContext
     */
    private void generateAndSaveAccessToken(UserLoginContext userLoginContext) {
        User user = userLoginContext.getEntity();
        Long userId = user.getUserId();
        String username = user.getUsername();
        String accessToken = JwtUtil.generateToken(username, UserConstants.LOGIN_USER_ID, userId, UserConstants.ONE_WEEK_MS);

        // 虽然JWT是无状态的，但是存储在redis中，每次登录生成新的JWT，也能实现单设备登录限制
        // 一个用户在其他地方登录会导致该设备下线，需重新登录
        redisTemplate.opsForValue().set(UserConstants.USER_LOGIN_TOKEN_PREFIX + userId, accessToken, UserConstants.ONE_WEEK_MS, TimeUnit.MILLISECONDS);

        userLoginContext.setAccessToken(accessToken);
    }
}




