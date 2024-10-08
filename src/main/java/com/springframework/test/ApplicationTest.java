package com.springframework.test;

import com.springframework.context.ApplicationContext;
import com.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.springframework.test.config.ApplicationConfig;
import com.springframework.test.service.TestService;
import com.springframework.test.service.TestServiceImpl;

/**
 * <p>测试启动类</p>

 */
public class ApplicationTest {
    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        TestService service = (TestService) applicationContext.getBean("testServiceImpl");

        service.echo();
    }
}