package com.springframework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = { java.lang.annotation.ElementType.TYPE })//写在类上
public @interface Scope {
    // 作用域
    String value() default "";

}
