package com.cloud.pro.server.modules.converter;

import com.cloud.pro.server.modules.context.share.CreateShareUrlContext;
import com.cloud.pro.server.modules.entity.Share;
import com.cloud.pro.server.modules.po.share.CreateShareUrlPO;
import com.cloud.pro.server.modules.vo.ShareUrlListVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 分享实体转化工具类
 */
@Mapper(componentModel = "spring")
public interface ShareConverter {

    @Mapping(target = "userId", expression = "java(com.cloud.pro.server.common.utils.UserIdUtil.get())")
    CreateShareUrlContext createShareUrlPO2Context(CreateShareUrlPO createShareUrlPO);

    ShareUrlListVO share2ShareUrlListVO(Share share);
}
