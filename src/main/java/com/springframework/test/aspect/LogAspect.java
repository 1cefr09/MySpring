package com.springframework.test.aspect;

import com.springframework.annotation.Component;
import com.springframework.annotation.aop.After;
import com.springframework.annotation.aop.Aspect;
import com.springframework.annotation.aop.Before;

@Component
@Aspect
public class LogAspect {

    @Before("execution(* com.springframework.test.service.*.*(..))")
    public void beforeMethod() {
        System.out.println("Before method execution");
    }

    @After("execution(* com.springframework.test.service.*.*(..))")
    public void afterMethod() {
        System.out.println("After method execution");
    }
}