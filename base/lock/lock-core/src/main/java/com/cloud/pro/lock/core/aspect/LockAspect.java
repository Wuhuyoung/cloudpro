package com.cloud.pro.lock.core.aspect;

import com.cloud.pro.core.exception.FrameworkException;
import com.cloud.pro.lock.core.LockContext;
import com.cloud.pro.lock.core.key.KeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 分布式锁 统一切面增强逻辑实现类
 */
@Component
@Aspect
@Slf4j
public class LockAspect implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Resource
    private LockRegistry lockRegistry;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 切点表达式
     */
    private static final String POINT_CUT = "@annotation(com.cloud.pro.lock.core.annotation.Lock)";

    /**
     * 切点模板方法
     */
    @Pointcut(value = POINT_CUT)
    public void lockPointcut() {

    }

    @Around("lockPointcut()")
    public Object aroundLock(ProceedingJoinPoint joinPoint) {
        Object result = null;
        // 1.获取上下文
        LockContext lockContext = LockContext.init(joinPoint);
        // 2.获取锁
        Lock lock = checkAndGetLock(lockContext);
        if (Objects.isNull(lock)) {
            log.error("lock aspect get lock fail.");
            throw new FrameworkException("aroundLock get lock fail.");
        }

        // 3.尝试加锁（有等待时间）
        boolean tryLock = false;
        try {
            tryLock = lock.tryLock(lockContext.getAnnotation().expireSecond(), TimeUnit.SECONDS);
            if (tryLock) { // 4.加锁成功，执行方法
                result = joinPoint.proceed(joinPoint.getArgs());
            }
        } catch (Throwable e) {
            log.error("lock aspect try lock fail.", e);
            throw new FrameworkException("aroundLock try lock fail.");
        } finally {
            if (tryLock) { // 5.释放锁
                lock.unlock();
            }
        }
        // 6.返回结果
        return result;
    }

    /**
     * 检查上下文的配置信息，返回锁实体
     * @param lockContext
     * @return
     */
    private Lock checkAndGetLock(LockContext lockContext) {
        if (Objects.isNull(lockRegistry)) {
            log.error("lockRegistry is not found...");
            return null;
        }
        String lockKey = getLockKey(lockContext);
        if (StringUtils.isBlank(lockKey)) {
            return null;
        }
        // 底层是一个ReentrantLock数组，默认大小为256
        Lock lock = lockRegistry.obtain(lockKey);
        return lock;
    }

    /**
     * 获取锁key
     * @param lockContext
     * @return
     */
    private String getLockKey(LockContext lockContext) {
        KeyGenerator keyGenerator = applicationContext.getBean(lockContext.getAnnotation().keyGenerator());
        if (Objects.nonNull(keyGenerator)) {
            return keyGenerator.generateKey(lockContext);
        }
        log.error("the keyGenerator is not found...");
        return StringUtils.EMPTY;
    }
}
