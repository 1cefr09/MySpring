package com.springframework.test.service;

import com.springframework.annotation.Autowired;
import com.springframework.annotation.Service;
import com.springframework.annotation.aop.Before;
import com.springframework.test.dao.TestDAO;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    TestDAO testDAO;


    public void echo() {
        System.out.println(testDAO.echo());
//        testDAO.echo();
    }
}
