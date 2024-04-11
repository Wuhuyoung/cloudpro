package com.cloud.pro.server.modules.vo;

import com.cloud.pro.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户基本信息VO对象
 */
@Data
public class UserVO implements Serializable {
    private static final long serialVersionUID = 268061823916143424L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户根目录的加密ID
     */
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long rootFileId;

    /**
     * 用户根目录名称
     */
    private String rootFileName;
}
