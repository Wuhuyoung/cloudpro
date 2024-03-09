package com.cloud.pro.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件夹表示枚举类
 */
@Getter
@AllArgsConstructor
public enum FolderFlagEnum {
    /**
     * 非文件夹
     */
    NO(0),
    /**
     * 文件夹
     */
    YES(1);

    private final Integer code;
}
