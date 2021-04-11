package com.smart.api.intercepter;

import javax.annotation.Resource;

import com.smart.api.intercepter.ratelimit.RateLimiterInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.smart.api.intercepter.auth.AuthInterceptor;
import com.smart.api.intercepter.request.RequestLogInterceptor;
import com.smart.api.intercepter.request.validator.ParameterValidatorInterceptor;

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
    private RateLimiterInterceptor rateLimiterInterceptor;
    @Resource
    private ParameterValidatorInterceptor parameterValidatorInterceptor;
    @Resource
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(preInterceptor).addPathPatterns("/**");
        registry.addInterceptor(requestLogInterceptor).addPathPatterns("/**");
        registry.addInterceptor(rateLimiterInterceptor).addPathPatterns("/**");
        registry.addInterceptor(parameterValidatorInterceptor).addPathPatterns("/**");
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
    }

}
