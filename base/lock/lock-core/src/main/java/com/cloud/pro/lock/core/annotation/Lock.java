package com.cloud.pro.lock.core.annotation;

import com.cloud.pro.lock.core.key.KeyGenerator;
import com.cloud.pro.lock.core.key.StandardKeyGenerator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义锁的注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Lock {

    /**
     * 锁的名称
     * @return
     */
    String name() default "";

    /**
     * 锁的过期时长
     * @return
     */
    long expireSecond() default 60L;

    /**
     * 锁的key，支持EL表达式
     * @return
     */
    String[] keys() default {};

    /**
     * 指定锁 key的生成器
     * @return
     */
    Class<? extends KeyGenerator> keyGenerator() default StandardKeyGenerator.class;
}
