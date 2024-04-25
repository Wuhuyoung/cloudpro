package com.cloud.pro.server.common.utils;

import com.cloud.pro.core.constants.CommonConstants;

import java.util.Objects;

/**
 * 分享ID存储工具类
 */
public class ShareIdUtil {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的分享ID
     * @param shareId
     */
    public static void set(Long shareId) {
        threadLocal.set(shareId);
    }

    /**
     * 获取当前线程的分享ID
     * @return
     */
    public static Long get() {
        Long shareId = threadLocal.get();
        if (Objects.isNull(shareId)) {
            return CommonConstants.ZERO_LONG;
        }
        return shareId;
    }
}
