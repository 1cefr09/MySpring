package com.springframework.aop;

import com.springframework.annotation.aop.Aspect;

import java.lang.reflect.Method;

public class AspectAdvisor implements Advisor {
    private final Class<?> aspectClass;
    private final Method beforeMethod;
    private final Method afterMethod;

    public AspectAdvisor(Class<?> aspectClass, Method beforeMethod, Method afterMethod) {
        this.aspectClass = aspectClass;
        this.beforeMethod = beforeMethod;
        this.afterMethod = afterMethod;
    }

    @Override
    public boolean matches(Class<?> beanClass) {
        // 匹配逻辑，例如检查类或方法上的注解
        return beanClass.isAnnotationPresent(Aspect.class);
    }

    @Override
    public void before() {
        try {
            beforeMethod.invoke(aspectClass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void after() {
        try {
            afterMethod.invoke(aspectClass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}