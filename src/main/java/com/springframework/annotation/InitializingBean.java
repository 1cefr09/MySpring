package com.springframework.annotation;

public interface InitializingBean {

    void afterPropertiesSet() throws Exception;

}
