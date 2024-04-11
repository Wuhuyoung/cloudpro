package com.cloud.pro.server.modules.converter;

import com.cloud.pro.server.modules.context.ChangePasswordContext;
import com.cloud.pro.server.modules.context.CheckAnswerContext;
import com.cloud.pro.server.modules.context.CheckUsernameContext;
import com.cloud.pro.server.modules.context.ResetPasswordContext;
import com.cloud.pro.server.modules.context.UserLoginContext;
import com.cloud.pro.server.modules.context.UserRegisterContext;
import com.cloud.pro.server.modules.entity.User;
import com.cloud.pro.server.modules.entity.UserFile;
import com.cloud.pro.server.modules.po.ChangePasswordPO;
import com.cloud.pro.server.modules.po.CheckAnswerPO;
import com.cloud.pro.server.modules.po.CheckUsernamePO;
import com.cloud.pro.server.modules.po.ResetPasswordPO;
import com.cloud.pro.server.modules.po.UserLoginPO;
import com.cloud.pro.server.modules.po.UserRegisterPO;
import com.cloud.pro.server.modules.vo.UserVO;
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

    CheckUsernameContext checkUsernamePO2checkUsernameContext(CheckUsernamePO checkUsernamePO);

    CheckAnswerContext checkAnswerPO2CheckAnswerContext(CheckAnswerPO checkAnswerPO);

    ResetPasswordContext resetPasswordPO2ResetPasswordContext(ResetPasswordPO resetPasswordPO);

    ChangePasswordContext changePasswordPO2ChangePasswordContext(ChangePasswordPO changePasswordPO);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "userFile.fileId", target = "rootFileId")
    @Mapping(source = "userFile.filename", target = "rootFileName")
    UserVO assembleUserVO(User user, UserFile userFile);
}