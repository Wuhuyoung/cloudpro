package com.cloud.pro.server.modules.converter;

import com.cloud.pro.server.modules.context.UserLoginContext;
import com.cloud.pro.server.modules.context.UserRegisterContext;
import com.cloud.pro.server.modules.entity.User;
import com.cloud.pro.server.modules.po.UserLoginPO;
import com.cloud.pro.server.modules.po.UserRegisterPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用户实体转化工具类
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    UserRegisterContext registerPO2RegisterContext(UserRegisterPO userRegisterPO);

    @Mapping(target = "password", ignore = true)
    User registerContext2User(UserRegisterContext userRegisterContext);

    UserLoginContext loginPO2LoginContext(UserLoginPO userLoginPO);
}
