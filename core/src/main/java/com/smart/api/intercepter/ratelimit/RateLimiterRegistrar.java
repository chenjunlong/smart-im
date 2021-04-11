package com.smart.api.intercepter.ratelimit;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author chenjunlong
 */
public class RateLimiterRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RateLimiterScanner scanner = new RateLimiterScanner();
        scanner.doScan("com.smart.api");
    }
}
