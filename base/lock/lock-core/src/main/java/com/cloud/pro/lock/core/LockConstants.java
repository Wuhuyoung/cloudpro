package com.cloud.pro.lock.core;

/**
 * 锁相关公用常量类
 */
public interface LockConstants {
    /**
     * 公用lock的名称
     */
    String CLOUD_PRO_LOCK = "cloud-pro-lock:";

    /**
     * 公用lock的路径
     * 主要针对zk等节点型
     */
    String CLOUD_PRO_LOCK_PATH = "/cloud-pro-lock";
}
