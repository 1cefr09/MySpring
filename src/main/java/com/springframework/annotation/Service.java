package com.springframework.annotation;

import java.lang.annotation.*;

/**
 * <p>服务模式注解</p>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {
    String value() default "";
}
