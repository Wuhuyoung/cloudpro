package com.cloud.pro.core.constants;

import org.apache.commons.lang3.StringUtils;

/**
 * 公共基础常量类
 * @author han
 */
public interface CommonConstants {
    /**
     * 字符串分隔符
     */
    String COMMON_SEPARATOR = "__,__";
    /**
     * 空字符串
     */
    String EMPTY_STR = StringUtils.EMPTY;

    /**
     * 点字符串
     */
    String POINT_STR = ".";

    /**
     * 斜杠字符串
     */
    String SLASH_STR = "/";

    Long ZERO_LONG = 0L;
    Integer ZERO_INT = 0;
    Integer ONE_INT = 1;
    Integer TWO_INT = 2;
    Integer MINUS_ONE_INT = -1;

    String TRUE_STR = "true";
    String FALSE_STR = "false";

    /**
     * 组件扫描基础路径
     */
    String BASE_COMPONENT_SCAN_PATH = "com.cloud.pro";

    /**
     * 公用加密字符串
     */
    String COMMON_ENCRYPT_STR = "****";
}
