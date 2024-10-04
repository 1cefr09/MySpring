package com.springframework.context.support;

import com.springframework.annotation.*;
import com.springframework.beans.BeanWrapper;
import com.springframework.beans.factory.config.BeanDefinition;
import com.springframework.beans.factory.support.BeanDefinitionReader;
import com.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>容器抽象类</p>
 * @author Bosen
 * @date 2021/9/10 15:19
 */
public abstract class AbstractApplicationContext extends DefaultListableBeanFactory implements ApplicationContext {

    protected BeanDefinitionReader reader;

    //继承父类的beanDefinitionMap，用于存放bd的map

    /**
     * <p>保存单例对象</p>
     */
    private Map<String, Object> factoryBeanObjectCache = new HashMap<>();

    /**
     * <p>保存包装对象</p>
     * BeanWrapper 包装了实际的 Bean 实例，并提供了对该实例的访问和操作方法
     */
    private Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

    @Override
    public void refresh() throws Exception {
        // 扫描需要扫描的包，并把相关的类转化为beanDefinition
        List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        // 注册，将beanDefinition放入IOC容器存储
        doRegisterBeanDefinition(beanDefinitions);
        // 将非懒加载的类初始化
        doAutowired();
    }

    /**
     * <p>将beanDefinition放入IOC容器存储</p>
     */
    private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitions) throws Exception {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception(beanDefinition.getFactoryBeanName() + "已经存在！");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    /**
     * <p>将非懒加载的类初始化</p>
     */
    private void doAutowired() {
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                getBean(beanName);//目的是通过 BeanDefinition 实例化 bean，并使用 BeanWrapper 包装，将 BeanWrapper 放入 factoryBeanInstanceCache 所以返回值用不到
            }
        }
    }

    @Override
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
        try {
            //下面会在instantiateBean判断是否已经实例化过，如果实例化过则直接返回
            // 通过bd实例化bean
            Object instance = instantiateBean(beanDefinition);
            if (instance == null) {
                return null;
            }
            // 将实例化后的bean使用bw包装
            BeanWrapper beanWrapper = new BeanWrapper(instance);

            this.factoryBeanInstanceCache.put(beanDefinition.getBeanClassName(), beanWrapper);

            // 开始注入操作
            populateBean(instance);

            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p>通过bd，实例化bean</p>
     */
    private Object instantiateBean(BeanDefinition beanDefinition) {
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        String factoryBeanName = beanDefinition.getFactoryBeanName();
        try {
            // 先判断单例池中是否存在该类的实例
            if (this.factoryBeanObjectCache.containsKey(factoryBeanName)) {
                instance = this.factoryBeanObjectCache.get(factoryBeanName);
            } else {
                Class<?> clazz = Class.forName(className);//使用 Class.forName 方法加载类
                instance = clazz.newInstance();

                this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(), instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    /**
     * <p>开始注入操作</p>
     */
    public void populateBean(Object instance) {
        Class clazz = instance.getClass();
        // 判断是否有Controller、Service、Component、Repository等注解标记
        if (!(clazz.isAnnotationPresent(Component.class) ||
                clazz.isAnnotationPresent(Controller.class) ||
                clazz.isAnnotationPresent(Service.class) ||
                clazz.isAnnotationPresent(Repository.class))) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            // 如果属性没有被Autowired标记，则跳过
            if (!field.isAnnotationPresent(Autowired.class)) {
                continue;
            }

            String autowiredBeanName = field.getType().getName();//先获取属性的类型，再获取类型的全类名

            field.setAccessible(true);

            try {
//                field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
                /*
                * 这里和原博客不一样，原博客是直接从缓存中获取实例，但是这样可能会导致循环依赖问题
                * 进行注入时，依赖的 bean 可能还没有实例化，所以这里需要判断一下
                * */
                // 尝试从缓存中获取实例
                //尝试过后，发现不能使用getBean调用，因为getBean需要传入beanName，而这里的beanName是autowiredBeanName也就是类名
                BeanWrapper beanWrapper = this.factoryBeanInstanceCache.get(autowiredBeanName);
                if (beanWrapper == null) {
                    // 如果没有找到，则调用 getBean 方法
                    String simpleBeanName = autowiredBeanName.substring(autowiredBeanName.lastIndexOf(".") + 1);
                    simpleBeanName = Character.toLowerCase(simpleBeanName.charAt(0)) + simpleBeanName.substring(1);
                    Object bean = getBean(simpleBeanName);
                    if (bean != null) {
                        field.set(instance, bean);
                    }
                } else {
                    field.set(instance, beanWrapper.getWrappedInstance());
                }
//                field.set(instance, beanWrapper.getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}