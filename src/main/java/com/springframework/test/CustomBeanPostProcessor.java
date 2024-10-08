package com.springframework.test;

import com.springframework.beans.factory.config.BeanPostProcessor;

public class CustomBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        // 在 bean 初始化之前执行的逻辑
//        System.out.println("Before Initialization: " + beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        // 在 bean 初始化之后执行的逻辑
//        System.out.println("After Initialization: " + beanName);
        return bean;
    }
}