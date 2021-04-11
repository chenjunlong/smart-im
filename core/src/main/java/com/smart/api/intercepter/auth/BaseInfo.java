package com.smart.api.intercepter.auth;

import com.smart.api.intercepter.constant.Constant;

import java.lang.annotation.*;
import java.lang.reflect.Method;

/**
 * @author chenjunlong
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BaseInfo {

    String desc() default "";

    boolean needAuth() default false;

    long rateLimit() default Constant.DEFAULT_RATE_LIMIT;

    String fallbackMethod() default "";
}
