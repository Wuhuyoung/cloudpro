package com.cloud.pro.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件合并标识 枚举类
 */
@Getter
@AllArgsConstructor
public enum MergeFlagEnum {
    /**
     * 不需要合并
     */
    NOT_READY(0),

    /**
     * 需要合并
     */
    READY(1);

    private final Integer code;
}
