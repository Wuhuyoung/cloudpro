package com.cloud.pro.lock.local.test;

import com.cloud.pro.core.constants.CommonConstants;
import com.cloud.pro.lock.core.LockConstants;
import com.cloud.pro.lock.local.test.instance.LockTester;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@SpringBootTest
@SpringBootApplication
@ComponentScan(basePackages = CommonConstants.BASE_COMPONENT_SCAN_PATH + ".lock")
public class LocalLockTest {
    @Resource
    private LockRegistry lockRegistry;

    @Resource
    private LockTester lockTester;

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 测试手动获取锁
     */
    @Test
    public void testLockRegistry() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            threadPoolTaskExecutor.execute(() -> {
                Lock lock = lockRegistry.obtain(LockConstants.CLOUD_PRO_LOCK);
                boolean tryLock = false;
                try {
                    tryLock = lock.tryLock(10L, TimeUnit.SECONDS);
                    if (tryLock) {
                        System.out.println(Thread.currentThread().getName() + " get the lock");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (tryLock) {
                        System.out.println(Thread.currentThread().getName() + " release the lock");
                        lock.unlock();
                    }
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
    }

    /**
     * 测试注解获取锁
     */
    @Test
    public void testLockAnnotation() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            threadPoolTaskExecutor.execute(() -> {
                lockTester.getName("thread" + finalI);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
    }
}
