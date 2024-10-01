package com.spring;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringApplicationContext {
    private Class configClass;
    private ConcurrentHashMap<String,Object> singletonObjects = new ConcurrentHashMap<>();//单例池
    private ConcurrentHashMap<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();//BeanDefinition池

    public SpringApplicationContext(Class configClass) {
        this.configClass = configClass;

        // 解析配置类
        //ComponentScan注解解析---》扫描路径---》扫描
        Scan(configClass);

        for (Map.Entry<String,BeanDefinition> entry:beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if (beanDefinition.getScope().equals("singleton")) {
                Object bean = createBean(beanDefinition);
                singletonObjects.put(beanName,bean);//放入单例池
            }else{
                //原型bean

            }
        }

    }

    public Object createBean(BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void Scan(Class configClass) {

        ComponentScan componentScan = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScan.value();//扫描路径
        path = path.replace(".","/");//扫描目录
//        System.out.println(path);
        //扫描
        ClassLoader classLoader = SpringApplicationContext.class.getClassLoader();
        URL resource =  classLoader.getResource(path);
        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            File[] files = file.listFiles();//获取目录下所有文件
            for (File f:files) {
                String fileName = f.getAbsolutePath();
                if (fileName.endsWith(".class")) {
                    String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));//截取类名
                    className = className.replace("\\", ".");//替换
//                    System.out.println(className);
                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        if (clazz.isAnnotationPresent(Component.class)) {//是否有Component注解
                            //表示这个类是个bean
                            //解析类，判断是单例bean还是prototype bean
                            //BeanDefinition
                            Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                            String beanName = componentAnnotation.value();//beanName
                            BeanDefinition beanDefinition = new BeanDefinition();
                            if (clazz.isAnnotationPresent(Scope.class)) {//是否有Scope注解
                                Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                beanDefinition.setScope(scopeAnnotation.value());
                            }else {
                                beanDefinition.setScope("singleton");
                            }
                            beanDefinition.setClazz(clazz);//
                            beanDefinitionMap.put(beanName,beanDefinition);
//                            System.out.println(beanName);
                        }

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Object getBean(String beanName) {

        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = (BeanDefinition) beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                //单例池
                Object o = singletonObjects.get(beanName);
                return o;
                }else {
                    //原型bean，创建对象
                    Object bean = createBean(beanDefinition);
                    return bean;
                }

        }else {
            throw new NullPointerException();

        }
    }
}
