package com.smart.api.intercepter.ratelimit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author chenjunlong
 */
public class RateLimiterUtils {

    private static final String CLASS_SEPARATOR = "#";

    public static String buildName(Class classz, Method method) {
        return classz.getName() + CLASS_SEPARATOR + method.getName();
    }

    public static String buildPath(Class classz, Method method) {

        String path = "";
        Annotation annotation = classz.getAnnotation(RequestMapping.class);
        if (annotation != null) {
            RequestMapping requestMapping = (RequestMapping) annotation;
            path = requestMapping.value()[0];
        }

        Annotation post = method.getAnnotation(PostMapping.class);
        if (post != null) {
            PostMapping postMapping = (PostMapping) post;
            path += postMapping.value()[0];
        }

        Annotation get = method.getAnnotation(GetMapping.class);
        if (get != null) {
            GetMapping getMapping = (GetMapping) get;
            path += getMapping.value()[0];
        }

        return path;
    }
}
