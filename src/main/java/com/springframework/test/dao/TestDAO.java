package com.springframework.test.dao;

import com.springframework.annotation.Repository;

@Repository
public class TestDAO {
    public String echo() {
        return "This is TestDAO#echo!!!";
    }
}
