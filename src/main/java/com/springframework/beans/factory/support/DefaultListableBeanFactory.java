package com.springframework.beans.factory.support;

import com.springframework.beans.factory.BeanFactory;
import com.springframework.beans.factory.config.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>bean工厂的实现类</p>
 */
public class DefaultListableBeanFactory implements BeanFactory {
    /**
     * <p>用于存放bd的map</p>
     */
    public final Map<String, BeanDefinition> beanDefinitionMap =
            new ConcurrentHashMap<>();

    @Override
    public Object getBean(String beanName) {
        return null;
    }
}