package com.springframework.aop.intercept;

import com.springframework.aop.JoinPoint;

import java.lang.reflect.Method;

public class MethodInvocation implements JoinPoint {
    private final Object target;
    private final Method method;
    private final Object[] args;

    public MethodInvocation(Object target, Method method, Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArgs() {
        return args;
    }
}