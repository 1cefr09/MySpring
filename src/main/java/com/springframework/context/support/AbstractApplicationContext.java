package com.springframework.context.support;

import com.springframework.annotation.*;
import com.springframework.beans.BeanWrapper;
import com.springframework.beans.factory.config.BeanDefinition;
import com.springframework.beans.factory.config.BeanPostProcessor;
import com.springframework.beans.factory.support.BeanDefinitionReader;
import com.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.springframework.context.ApplicationContext;
import com.springframework.beans.factory.ObjectFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractApplicationContext extends DefaultListableBeanFactory implements ApplicationContext {

    protected BeanDefinitionReader reader;
    /*
    * 原博客并没有singletonObjects这个单例池，导致每次getBean时不能直接判断是否实例化过再取回，而是要重复实例化、注入、PostProcessor，这样会导致重复创建代理的问题
    * */

    // 一级缓存：存放完全初始化好的单例Bean
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    // 二级缓存：存放提前暴露的原始Bean（尚未填充属性）
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();

    // 三级缓存：存放Bean工厂，用于解决循环依赖
    private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>();

    // 当前正在创建的Bean名称集合
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>());


//    private Map<String,Object> singletonObjects = new ConcurrentHashMap<>();//用于缓存已经实例化的 Bean 对象。键是 Bean 的名称，值是对应的 Bean 实例。
    private Map<String, Object> factoryBeanObjectCache = new HashMap<>();//用于缓存已经实例化的 Bean 对象。键是 Bean 的工厂名称，值是对应的 Bean 实例。这个缓存用于避免重复实例化相同的 Bean
    private Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();//用于缓存已经包装成 BeanWrapper 的 Bean 实例。键是 Bean 的类名，值是对应的 BeanWrapper 实例。这个缓存用于在依赖注入时快速获取已经包装好的 Bean 实例。
    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    @Override
    public void refresh() throws Exception {
        // 1. 加载Bean定义
        List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        // 2. 注册Bean定义
        doRegisterBeanDefinition(beanDefinitions);

        // 3. 注册BeanPostProcessor
        registerBeanPostProcessors(beanDefinitions);

        // 4. 预初始化单例Bean
        doAutowired();
    }


    private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitions) throws Exception {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());

            // 只注册带有@Component及其派生注解的类
            if (!hasComponentAnnotation(clazz)) {
                continue;
            }

            String beanName = beanDefinition.getFactoryBeanName();
            if (super.beanDefinitionMap.containsKey(beanName)) {
                throw new Exception("Bean名称已存在: " + beanName);
            }

            super.beanDefinitionMap.put(beanName, beanDefinition);
        }
    }
    private boolean hasComponentAnnotation(Class<?> clazz) {
        return clazz.isAnnotationPresent(Component.class) ||
                clazz.isAnnotationPresent(Controller.class) ||
                clazz.isAnnotationPresent(Service.class) ||
                clazz.isAnnotationPresent(Repository.class);
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
        // 1. 检查一级缓存
        Object bean = singletonObjects.get(beanName);
        if (bean != null) {
            return bean;
        }

        // 2. 检查是否正在创建中（循环依赖处理）
        if (isSingletonCurrentlyInCreation(beanName)) {
            bean = earlySingletonObjects.get(beanName);
            if (bean == null) {
                ObjectFactory<?> factory = singletonFactories.get(beanName);
                if (factory != null) {
                    bean = factory.getObject();
                    earlySingletonObjects.put(beanName, bean);
                    singletonFactories.remove(beanName);
                }
            }
            return bean;
        }

        // 3. 正常创建流程
        BeanDefinition bd = super.beanDefinitionMap.get(beanName);
        if (bd == null) {
            throw new RuntimeException("未定义的Bean: " + beanName);
        }

        beforeSingletonCreation(beanName);
        try {
            bean = createBean(beanName, bd);
            singletonObjects.put(beanName, bean);
            return bean;
        } finally {
            afterSingletonCreation(beanName);
        }
    }

    protected Object createBean(String beanName, BeanDefinition bd) {
        try {
            // 1. 实例化
            Object bean = createBeanInstance(beanName, bd);

            // 2. 添加三级缓存
            addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, bd, bean));

            // 3. 属性注入
            populateBean(bean);

            // 4. 初始化
            return initializeBean(beanName, bean);
        } catch (Exception e) {
            throw new RuntimeException("创建Bean失败: " + beanName, e);
        }
    }

    private Object createBeanInstance(String beanName, BeanDefinition bd) throws Exception {
        String className = bd.getBeanClassName();
        Class<?> clazz = Class.forName(className);
        return clazz.newInstance();
    }

    protected void addSingletonFactory(String beanName, ObjectFactory<?> factory) {
        if (!singletonObjects.containsKey(beanName)) {
            singletonFactories.put(beanName, factory);
            earlySingletonObjects.remove(beanName);
        }
    }

    protected Object getEarlyBeanReference(String beanName, BeanDefinition beanDefinition, Object bean) {
        // 这里可以应用后置处理器等逻辑
        return bean;
    }

    protected void beforeSingletonCreation(String beanName) {
        if (!this.singletonsCurrentlyInCreation.add(beanName)) {
            throw new RuntimeException("Circular reference detected for bean '" + beanName + "'");
        }
    }

    protected void afterSingletonCreation(String beanName) {
        this.singletonsCurrentlyInCreation.remove(beanName);
    }

    protected boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    protected Object initializeBean(String beanName, Object bean) throws Exception {
        // 1. 前置处理
        bean = applyPostProcessorsBeforeInitialization(bean, beanName);

        // 2. 初始化方法调用（可扩展）
        // invokeInitMethods(bean, bd);

        // 3. 后置处理
        return applyPostProcessorsAfterInitialization(bean, beanName);
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

    private Object applyPostProcessorsBeforeInitialization(Object bean, String beanName) throws Exception {
        for (BeanPostProcessor processor : beanPostProcessors) {
            bean = processor.postProcessBeforeInitialization(bean, beanName);
            if (bean == null) return null;
        }
        return bean;
    }

    private Object applyPostProcessorsAfterInitialization(Object bean, String beanName) throws Exception {
        for (BeanPostProcessor processor : beanPostProcessors) {
            bean = processor.postProcessAfterInitialization(bean, beanName);
            if (bean == null) return null;
        }
        return bean;
    }
}