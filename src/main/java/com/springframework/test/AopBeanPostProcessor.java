package com.springframework.test;

import com.springframework.beans.factory.config.BeanPostProcessor;

public class AopBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        /*
        * 简易Aop的PostProcessor，要先定义JoinPoint和PointCut，获取类和方法进行比对，如果匹配就创建代理类
        * 后面还要对factoryBeanInstanceCache等Bean池进行更新，确保是代理对象而不是原始对象
        * 由于 factoryBeanInstanceCache 是私有的，不能直接从 BeanPostProcessor 中修改它，必须通过公开的setBean 方法来间接修改
        * */
        return null;
    }
}
