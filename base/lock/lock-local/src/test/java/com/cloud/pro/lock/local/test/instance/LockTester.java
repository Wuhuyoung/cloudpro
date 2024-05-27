package com.cloud.pro.lock.local.test.instance;

import com.cloud.pro.lock.core.annotation.Lock;
import org.springframework.stereotype.Component;

@Component
public class LockTester {

    @Lock(name = "test", keys = "#name", expireSecond = 10L)
    public void getName(String name) {
        System.out.println(Thread.currentThread().getName() + " get the lock");
        System.out.println("Hello " + name);
        System.out.println(Thread.currentThread().getName() + " release the lock");
    }
}
