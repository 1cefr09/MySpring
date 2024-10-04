package com.springframework.test.controller;

import com.springframework.annotation.Autowired;
import com.springframework.annotation.Controller;
import com.springframework.test.service.TestService;

@Controller
public class TestController {
    @Autowired
    private TestService testService;

}
