package com.smart.api.intercepter.ratelimit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.smart.api.exception.ApiRateLimitException;
import com.smart.api.intercepter.constant.Constant;
import com.smart.api.intercepter.request.validator.*;
import com.smart.common.util.ClassUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.smart.api.exception.AuthFailException;
import com.smart.api.exception.ExcepFactor;
import com.smart.api.intercepter.auth.BaseInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author chenjunlong
 */
@Component
public class RateLimiterInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        long limit = Constant.DEFAULT_RATE_LIMIT;
        BaseInfo baseInfo = handlerMethod.getMethodAnnotation(BaseInfo.class);
        if (baseInfo != null) {
            limit = baseInfo.rateLimit();
        }

        Class clazz = handlerMethod.getBean().getClass();
        String counterPath = RateLimiterUtils.buildPath(clazz, handlerMethod.getMethod());
        QpsCounter qpsCounter = QpsStorage.get(counterPath);
        if(qpsCounter == null){
            return true;
        }

        long currQps = QpsStorage.get(counterPath).incr(System.currentTimeMillis(), 1L);
        if (currQps > limit) {
            String fallbackMethod = baseInfo.fallbackMethod();
            if (StringUtils.isBlank(fallbackMethod)) {
                throw new ApiRateLimitException(ExcepFactor.E_API_RATE_LIMIT);
            }

            Parameter[] parameters = handlerMethod.getMethod().getParameters();
            Class<?>[] parameterTypes = new Class[parameters.length];
            Object[] parameterValues = new Object[parameters.length];
            this.buildParameters(request, parameters, parameterTypes, parameterValues);

            Object result = this.fallback(clazz.getName(), fallbackMethod, parameterTypes, parameterValues);
            throw new ApiRateLimitException(ExcepFactor.E_API_RATE_LIMIT, result);
        }
        return true;
    }

    private void buildParameters(HttpServletRequest request, Parameter[] parameters, Class<?>[] parameterTypes, Object[] parameterValues) {
        int idx = 0;
        for (Parameter parameter : parameters) {
            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
            if (requestParam != null) {
                String value = request.getParameter(requestParam.value());
                parameterTypes[idx] = parameter.getType();
                if (parameter.getType() == Long.class) {
                    parameterValues[idx] = Long.parseLong(value);
                } else if (parameter.getType() == Integer.class) {
                    parameterValues[idx] = Integer.parseInt(value);
                } else if (parameter.getType() == Short.class) {
                    parameterValues[idx] = Short.parseShort(value);
                } else if (parameter.getType() == Double.class) {
                    parameterValues[idx] = Double.parseDouble(value);
                } else if (parameter.getType() == Float.class) {
                    parameterValues[idx] = Float.parseFloat(value);
                } else if (parameter.getType() == Boolean.class) {
                    parameterValues[idx] = Boolean.parseBoolean(value);
                } else {
                    parameterValues[idx] = value;
                }
                idx++;
            }
        }
    }

    private Object fallback(String clazzName, String fallbackMethod, Class<?>[] parameterTypes, Object[] parameterValues) {
        try {
            Class clazz = Class.forName(clazzName);
            Object instance = clazz.newInstance();
            Method method = instance.getClass().getDeclaredMethod(fallbackMethod, parameterTypes);
            return method.invoke(instance, parameterValues);
        } catch (Exception e) {
            return null;
        }
    }

}
