package com.springframework.annotation;

import java.lang.annotation.*;

/**
 * <p>通用组件模式注解</p>
 * @author Bosen
 * @date 2021/9/10 14:31
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface Component {
    String value() default "";
}
