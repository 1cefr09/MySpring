package com.springframework.beans.factory;

/**
 * <p>spring顶层接口</p>
 */
public interface BeanFactory {
    /**
     * <p>通过bean名称获取bean实例</p>
     */
    Object getBean(String beanName);
}
