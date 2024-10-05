package com.springframework.beans.factory.support;

import com.springframework.beans.factory.config.BeanDefinition;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>用于扫描bd</p>
 */
public class BeanDefinitionReader {
    /**
     * <p>存储扫描出来的bean的全类名</p>
     */
    private List<String> registryBeanClasses = new ArrayList<>();

    public BeanDefinitionReader(String scanPackage) throws Exception {
        doScan(scanPackage);
    }

    /**
     * <p>扫描包下的类</p>
     * @param scanPackage 包名
     */
    public void doScan(String scanPackage) throws Exception {
        // 将包名转为文件路径
        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        if (url == null) {
            throw new Exception("包" + scanPackage + "不存在！");
        }
        File classPath = new File(url.getFile());//使用包的 URL 创建一个 File 对象 classPath
        for (File file : classPath.listFiles()) {//遍历文件夹：遍历 classPath 目录下的所有文件和子目录
            if (file.isDirectory()) {//如果是子目录，递归调用 doScan 方法
                doScan(scanPackage + "." +file.getName());
            } else {//如果是 .class 文件，将其全限定名添加到 registryBeanClasses 列表中
                if (!file.getName().endsWith(".class")) {
                    // 如果不是class文件则跳过
                    continue;
                }
                String className = scanPackage + "." + file.getName().replace(".class", "");
                registryBeanClasses.add(className);
            }
        }
    }

    /**
     * <p>将扫描到的类信息转化为bd对象</p>
     */
    public List<BeanDefinition> loadBeanDefinitions() {
        List<BeanDefinition> result = new ArrayList<>();
        try {
            for (String className : registryBeanClasses) {//遍历 registryBeanClasses 列表中的每个类名
                Class<?> beanClass = Class.forName(className);//使用 Class.forName 方法加载类
                if (beanClass.isInterface()) {
                    // 如果是接口则跳过
                    continue;
                }
                //调用 doCreateBeanDefinition 方法创建 BeanDefinition 对象，并将其添加到结果列表中
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));

//                Class<?>[] interfaces = beanClass.getInterfaces();//获取类实现的所有接口
//                for (Class<?> anInterface : interfaces) {//
//                    result.add(doCreateBeanDefinition(anInterface.getName(), beanClass.getName()));
//                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * <p>将类信息转化为beanDefinition</p>
     * 在创建 BeanDefinition 时传入两个名称的原因是为了区分 FactoryBeanName 和 BeanClassName，它们分别代表不同的含义：
     * FactoryBeanName: 这是在 IOC 容器中存储 Bean 的键值，通常是类名的��字母小写形式或接口名。它用于在容器中唯一标识一个 Bean 实例。
     * BeanClassName: 这是 Bean 对应的全类名，用于反射创建 Bean 实例。
     */

    public BeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    /**
     * <p>将类名首字母小写</p>
     */
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}