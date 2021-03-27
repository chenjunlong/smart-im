package com.smart.api.auth;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author chenjunlong
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface BaseInfo {

    String desc() default "";

    boolean needAuth() default false;
}
