package com.cloud.pro.core.utils;

import com.cloud.pro.core.constants.CommonConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * 文件相关工具类
 */
public class FileUtil {
    /**
     * 获取文件后缀
     * @param filename
     * @return
     */
    public static String getFileSuffix(String filename) {
        if (StringUtils.isBlank(filename) ||
                filename.indexOf(CommonConstants.POINT_STR) == CommonConstants.MINUS_ONE_INT) {
            return StringUtils.EMPTY;
        }
        return filename.substring(filename.lastIndexOf(CommonConstants.POINT_STR));
    }
}
