package com.smart.api.intercepter.ratelimit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import com.smart.api.intercepter.auth.BaseInfo;
import com.smart.api.intercepter.constant.Constant;

/**
 * @author chenjunlong
 */
public class RateLimiterScanner {

    private static final Logger infoLog = LoggerFactory.getLogger("info");

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);

    public void doScan(String scanPackage) {
        String scanPackagePath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(scanPackage)) + "/**/*Controller.class";

        Resource[] resources = new Resource[0];
        try {
            resources = this.resourcePatternResolver.getResources(scanPackagePath);
        } catch (IOException e) {

        }

        for (int i = 0; i < resources.length; i++) {
            Resource resource = resources[i];
            if (resource.isReadable()) {
                MetadataReader metadataReader = null;
                try {
                    metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                    Class classz = Class.forName(metadataReader.getClassMetadata().getClassName());
                    Method[] methods = classz.getMethods();
                    if (ArrayUtils.isEmpty(methods)) {
                        continue;
                    }
                    for (Method method : methods) {
                        Annotation annotation = method.getAnnotation(BaseInfo.class);
                        if (null != annotation) {
                            String counterName = classz.getName() + Constant.CLASS_SEPARATOR + method.getName();
                            QpsStorage.create(counterName);
                            infoLog.info(" QpsCounter#create: {}", counterName);
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
    }
}
