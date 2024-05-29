package com.cloud.pro.server.modules.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户搜索历史 VO对象
 */
@Data
public class UserSearchHistoryVO implements Serializable {
    private static final long serialVersionUID = -1423699125694627208L;

    /**
     * 搜索文案
     */
    private String value;
}
