package com.springframework.test.service;

import com.springframework.annotation.Autowired;
import com.springframework.annotation.Service;

@Service
public class TestServece1Impl implements TestService1 {

    @Autowired
    TestService testService;

    public void echo() {
        System.out.println("TestService1 echo");
    }
}
