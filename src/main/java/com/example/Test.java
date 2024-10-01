package com.example;

import com.spring.SpringApplicationContext;

public class Test {
    public static void main(String[] args) {
        SpringApplicationContext context = new SpringApplicationContext(AppConfig.class);
        System.out.println(context.getBean("userService"));

    }
}
