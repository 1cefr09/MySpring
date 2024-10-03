package com.springframework.annotation;

import java.lang.annotation.*;

/**
 * <p>数据仓库模式注解</p>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Repository {
    String value() default "";
}
