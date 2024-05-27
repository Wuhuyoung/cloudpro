package com.cloud.pro.lock.core.key;

import com.cloud.pro.lock.core.LockContext;

/**
 * 锁的key生成器 顶级接口
 */
public interface KeyGenerator {

    /**
     * 生成锁的Key
     * @param lockContext
     * @return
     */
    String generateKey(LockContext lockContext);
}
