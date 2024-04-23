package com.cloud.pro.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件删除标识枚举类
 */
@AllArgsConstructor
@Getter
public enum DelFlagEnum {
    /**
     * 未删除
     */
    NO(0),
    /**
     * 已删除
     */
    YES(1),

    /**
     * 所有文件
     */
    YES_AND_NO(2);

    private Integer code;
}
