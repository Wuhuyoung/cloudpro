package com.cloud.pro.lock.core;

import com.cloud.pro.lock.core.annotation.Lock;
import lombok.Data;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * 锁实体的上下文信息
 * 主要做切点的实体解析，为整体逻辑所公用
 */
@Data
public class LockContext {
    /**
     * 切点方法所属的类的名称
     */
    private String className;

    /**
     * 切点方法的名称
     */
    private String methodName;

    /**
     * 切点方法上标注的自定义锁注解
     */
    private Lock annotation;

    /**
     * 类的Class对象
     */
    private Class<?> classType;

    /**
     * 当前调用的方法实体对象
     */
    private Method method;

    /**
     * 参数列表实体
     */
    private Object[] args;

    /**
     * 参数列表类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 代理对象实体
     */
    private Object target;

    /**
     * 初始化实体对象
     * @param proceedingJoinPoint
     * @return
     */
    public static LockContext init(ProceedingJoinPoint proceedingJoinPoint) {
        LockContext lockContext = new LockContext();

        Signature signature = proceedingJoinPoint.getSignature();
        Object[] args = proceedingJoinPoint.getArgs();
        Object target = proceedingJoinPoint.getTarget();
        String methodName = signature.getName();
        Class classType = signature.getDeclaringType();
        String className = signature.getDeclaringTypeName();
        Method method = ((MethodSignature) signature).getMethod();
        Lock annotation = method.getAnnotation(Lock.class);
        Class[] parameterTypes = ((MethodSignature) signature).getParameterTypes();

        lockContext.setClassName(className);
        lockContext.setMethodName(methodName);
        lockContext.setAnnotation(annotation);
        lockContext.setClassType(classType);
        lockContext.setMethod(method);
        lockContext.setArgs(args);
        lockContext.setParameterTypes(parameterTypes);
        lockContext.setTarget(target);
        return lockContext;
    }
}
