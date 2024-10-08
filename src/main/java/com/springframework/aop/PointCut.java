package com.springframework.aop;

import java.lang.reflect.Method;

public interface PointCut {
    boolean matches(Class<?> targetClass, Method method);
}