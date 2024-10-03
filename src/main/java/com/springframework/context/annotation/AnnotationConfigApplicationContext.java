package com.springframework.context.annotation;

import com.springframework.beans.factory.support.BeanDefinitionReader;
import com.springframework.context.support.AbstractApplicationContext;

/**
 * <p>基于注解作为配置的容器</p>
 */
public class AnnotationConfigApplicationContext extends AbstractApplicationContext {

    public AnnotationConfigApplicationContext(Class annotatedClass) throws Exception {
        // 初始化父类bdw
        super.reader = new BeanDefinitionReader(getScanPackage(annotatedClass));
        refresh();
    }

    @Override
    public void refresh() throws Exception {
        // 交给父类完成
        super.refresh();
    }

    /**
     * <p>传入一个class，获取其@ComponentScan注解中的value值</p>
     *
     */
    public String getScanPackage(Class annotatedClass) throws Exception {
        // 判断是否有ComponentScan注解
        if (!annotatedClass.isAnnotationPresent(ComponentScan.class)) {
            throw new Exception("请为注解配置类加上@ComponentScan注解！");
        }
        ComponentScan componentScan =
                (ComponentScan) annotatedClass.getAnnotation(ComponentScan.class);
        return componentScan.value().trim();//去除其前后的空格后返回
    }
}