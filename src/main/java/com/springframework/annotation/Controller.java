package com.springframework.annotation;

import java.lang.annotation.*;

/**
 * <p>Web控制器模式注解</p>

 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Controller {
    String value() default "";
}
