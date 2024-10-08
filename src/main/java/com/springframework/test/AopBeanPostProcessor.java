package com.springframework.test;

import com.springframework.annotation.aop.After;
import com.springframework.annotation.aop.Before;
import com.springframework.aop.ExecutionPointCut;
import com.springframework.aop.PointCut;
import com.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class AopBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        // 获取所有的 PointCut 实例
        List<PointCut> pointCuts = getPointCuts();
        if (bean.getClass().getInterfaces().length == 0) {// 判断 bean 是否实现了接口
            return bean;
        }
        // 遍历 PointCut 实例
        for (PointCut pointCut : pointCuts) {
            Method[] methods = bean.getClass().getMethods();
            for (Method method : methods) {
                // 检查当前 bean 的方法是否匹配
                if (pointCut.matches(bean.getClass(), method)) {
                    // 如果匹配，则创建代理对象
                    System.out.println("Creating proxy for bean: " + beanName);
                    return Proxy.newProxyInstance(
                            bean.getClass().getClassLoader(),
                            bean.getClass().getInterfaces(),
                            new AspectInvocationHandler(bean, pointCut)
                    );
                }
            }
        }
        return bean;
    }

    private List<PointCut> getPointCuts() {
        List<PointCut> pointCuts = new ArrayList<>();
        pointCuts.add(new ExecutionPointCut("com.springframework.test.service.TestServiceImpl.echo"));
        return pointCuts;
    }

    private static class AspectInvocationHandler implements InvocationHandler {
        private final Object target;
        private final PointCut pointCut;

        public AspectInvocationHandler(Object target, PointCut pointCut) {
            this.target = target;
            this.pointCut = pointCut;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 在方法执行前后添加切面逻辑
            if (pointCut.matches(target.getClass(), method)) {
                // 执行前置通知
                System.out.println("前置方法");
                // 执行目标方法
                Object result = method.invoke(target, args);
                // 执行后置通知
                return result;
            }
            return method.invoke(target, args);
        }
    }
}