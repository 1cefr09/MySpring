package com.springframework.aop;

import java.lang.reflect.Method;

public class ExecutionPointCut implements PointCut {
    private final String expression;

    public ExecutionPointCut(String expression) {
        this.expression = expression;
    }

    @Override
    public boolean matches(Class<?> targetClass, Method method) {
        // 获取类的全限定名
        String className = targetClass.getName();
        // 获取方法名
        String methodName = method.getName();
        // 构建完整的方法签名
        String fullMethodName = className + "." + methodName;
        // 匹配完整的方法签名
        return fullMethodName.equals(expression);
    }
}