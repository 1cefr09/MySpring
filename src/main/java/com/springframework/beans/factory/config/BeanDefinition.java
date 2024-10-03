package com.springframework.beans.factory.config;

/**
 * <p>保存bean定义相关的信息</p>
 * @author Bosen
 * @date 2021/9/10 14:41
 */
public class BeanDefinition {
    /**
     * <p>bean对应的全类名</p>
     */
    private String beanClassName;

    /**
     * <p>是否懒加载</p>
     */
    private boolean lazyInit = false;

    /**
     * <p>保存在IOC容器时的key值</p>
     */
    private String factoryBeanName;

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
