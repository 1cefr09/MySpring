package com.springframework.annotation;

import java.lang.annotation.*;

/**
 * <p>参数注解</p>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value() default "";
}