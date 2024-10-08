package com.springframework.context.support;

import com.springframework.annotation.*;
import com.springframework.beans.BeanWrapper;
import com.springframework.beans.factory.config.BeanDefinition;
import com.springframework.beans.factory.config.BeanPostProcessor;
import com.springframework.beans.factory.support.BeanDefinitionReader;
import com.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractApplicationContext extends DefaultListableBeanFactory implements ApplicationContext {

    protected BeanDefinitionReader reader;
    /*
    * 原博客并没有singletonObjects这个单例池，导致每次getBean时不能直接判断是否实例化过再取回，而是要重复实例化、注入、PostProcessor，这样会导致重复创建代理的问题
    * */
    private Map<String,Object> singletonObjects = new ConcurrentHashMap<>();//用于缓存已经实例化的 Bean 对象。键是 Bean 的名称，值是对应的 Bean 实例。
    private Map<String, Object> factoryBeanObjectCache = new HashMap<>();//用于缓存已经实例化的 Bean 对象。键是 Bean 的工厂名称，值是对应的 Bean 实例。这个缓存用于避免重复实例化相同的 Bean
    private Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();//用于缓存已经包装成 BeanWrapper 的 Bean 实例。键是 Bean 的类名，值是对应的 BeanWrapper 实例。这个缓存用于在依赖注入时快速获取已经包装好的 Bean 实例。
    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    @Override
    public void refresh() throws Exception {
        List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        doRegisterBeanDefinition(beanDefinitions);
        registerBeanPostProcessors(beanDefinitions);
        doAutowired();
    }

    private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitions) throws Exception {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
            if (!(clazz.isAnnotationPresent(Component.class) ||
                    clazz.isAnnotationPresent(Controller.class) ||
                    clazz.isAnnotationPresent(Service.class) ||
                    clazz.isAnnotationPresent(Repository.class))) {
                continue;
            }
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception(beanDefinition.getFactoryBeanName() + "已经存在！");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    private void registerBeanPostProcessors(List<BeanDefinition> beanDefinitions) throws Exception {//注册BeanPostProcessor
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
            if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                this.beanPostProcessors.add((BeanPostProcessor) clazz.newInstance());
            }
        }
    }

    private void doAutowired() {
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                getBean(beanName);
            }
        }
    }

    @Override
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
        try {
            if(this.singletonObjects.containsKey(beanName)){//如果已经实例化过，直接返回，区别于原博客的并没有这个单例池
                return this.singletonObjects.get(beanName);
            }
            Object instance = instantiateBean(beanDefinition);
            if (instance == null) {
                return null;
            }
            BeanWrapper beanWrapper = new BeanWrapper(instance);
            this.factoryBeanInstanceCache.put(beanDefinition.getBeanClassName(), beanWrapper);
            instance = applyBeanPostProcessorsBeforeInitialization(instance, beanName);//实例化之后，属性填充之前调用
            populateBean(instance);
            instance = applyBeanPostProcessorsAfterInitialization(instance, beanName);//实例化和属性填充之后调用
            singletonObjects.put(beanName,instance);
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object instantiateBean(BeanDefinition beanDefinition) {
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        String factoryBeanName = beanDefinition.getFactoryBeanName();
        try {
            if (this.factoryBeanObjectCache.containsKey(factoryBeanName)) {
                instance = this.factoryBeanObjectCache.get(factoryBeanName);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(), instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public void populateBean(Object instance) {
        Class<?> clazz = instance.getClass();
        if (!(clazz.isAnnotationPresent(Component.class) ||
                clazz.isAnnotationPresent(Controller.class) ||
                clazz.isAnnotationPresent(Service.class) ||
                clazz.isAnnotationPresent(Repository.class))) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Autowired.class)) {
                continue;
            }
            Class<?> fieldType = field.getType();
            String autowiredBeanName = field.getType().getName();
            field.setAccessible(true);

            try {
                BeanWrapper beanWrapper = this.factoryBeanInstanceCache.get(autowiredBeanName);
                if (beanWrapper == null) {
                    if (fieldType.isInterface()) {
                        List<Class<?>> implClasses = findImplementationClasses(fieldType);
                        if (implClasses.size() > 1) {
                            if (field.isAnnotationPresent(Qualifier.class)) {
                                String qualifierValue = field.getAnnotation(Qualifier.class).value();
                                for (Class<?> implClass : implClasses) {
                                    if (implClass.getSimpleName().equals(qualifierValue)) {
                                        Object bean = getBean(implClass.getSimpleName());
                                        if (bean != null) {
                                            field.set(instance, bean);
                                        }
                                        break;
                                    }
                                }
                            } else {
                                throw new Exception("多个实现类存在，请使用 @Qualifier 注解指定具体的实现类: " + fieldType.getName());
                            }
                        } else {
                            String simpleBeanName = implClasses.get(0).getSimpleName();
                            simpleBeanName = Character.toLowerCase(simpleBeanName.charAt(0)) + simpleBeanName.substring(1);
                            Object bean = getBean(simpleBeanName);
                            if (bean != null) {
                                field.set(instance, bean);
                            }
                        }
                    } else {
                        String simpleBeanName = autowiredBeanName.substring(autowiredBeanName.lastIndexOf(".") + 1);
                        simpleBeanName = Character.toLowerCase(simpleBeanName.charAt(0)) + simpleBeanName.substring(1);
                        Object bean = getBean(simpleBeanName);
                        if (bean != null) {
                            field.set(instance, bean);
                        }
                    }
                } else {
                    field.set(instance, beanWrapper.getWrappedInstance());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<Class<?>> findImplementationClasses(Class<?> interfaceClass) throws Exception {
        List<Class<?>> implementationClasses = new ArrayList<>();
        for (BeanDefinition beanDefinition : super.beanDefinitionMap.values()) {
            Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
            if (interfaceClass.isAssignableFrom(clazz) && !clazz.isInterface()) {
                implementationClasses.add(clazz);
            }
        }
        if (implementationClasses.isEmpty()) {
            throw new Exception("未找到接口的实现类: " + interfaceClass.getName());
        }
        return implementationClasses;
    }

    private Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws Exception {
        Object result = existingBean;
        for (BeanPostProcessor processor : this.beanPostProcessors) {
            result = processor.postProcessBeforeInitialization(result, beanName);
            if (result == null) return null;
        }
        return result;
    }

    private Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws Exception {
        Object result = existingBean;
        for (BeanPostProcessor processor : this.beanPostProcessors) {
            result = processor.postProcessAfterInitialization(result, beanName);
            if (result == null) return null;
        }
        return result;
    }
}