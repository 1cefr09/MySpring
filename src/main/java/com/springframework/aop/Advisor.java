package com.springframework.aop;

public interface Advisor {
    boolean matches(Class<?> beanClass);
    void before();
    void after();
}