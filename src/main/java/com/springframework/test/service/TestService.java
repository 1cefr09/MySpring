package com.springframework.test.service;

import com.springframework.annotation.Autowired;
import com.springframework.annotation.Service;
import com.springframework.test.dao.TestDAO;

@Service
public class TestService {
    @Autowired
    TestDAO testDAO;

    public void echo() {
        System.out.println(testDAO.echo());
    }
}
