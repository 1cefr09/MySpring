package com.springframework.test.controller;

import com.springframework.annotation.Autowired;
import com.springframework.annotation.Controller;
import com.springframework.annotation.RequestMapping;
import com.springframework.annotation.RequestParam;
import com.springframework.test.service.TestService;
import com.springframework.test.service.TestServiceImpl;
import com.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
public class TestController {

    @Autowired
    private TestService testService;

}
