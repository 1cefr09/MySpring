package com.springframework.annotation;

import java.lang.annotation.*;

/**
 * <p>自动注入注解</p>
 * @author Bosen
 * @date 2021/9/10 14:23
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    String value() default "";
}