package com.springframework.annotation;

import java.lang.annotation.*;

/**
 * <p>为Spring的模式注解添加索引</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Indexed {}