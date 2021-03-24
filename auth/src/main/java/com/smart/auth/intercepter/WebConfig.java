package com.smart.auth.intercepter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author chenjunlong
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private PreInterceptor preInterceptor;
    @Resource
    private RequestLogInterceptor requestLogInterceptor;
    @Resource
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(preInterceptor).addPathPatterns("/**");
        registry.addInterceptor(requestLogInterceptor).addPathPatterns("/**");
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
    }

}
