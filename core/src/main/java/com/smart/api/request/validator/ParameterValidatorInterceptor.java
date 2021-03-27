package com.smart.api.request.validator;

import com.smart.api.annotation.ParamDesc;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenjunlong
 */
@Slf4j
@Component
public class ParameterValidatorInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        MethodParameter[] parameters = handlerMethod.getMethodParameters();
        if (ArrayUtils.isEmpty(parameters)) {
            return true;
        }

        List<ParameterValidator> validators = new ArrayList<>(parameters.length);
        for (MethodParameter parameter : parameters) {
            RequestParam param = parameter.getParameterAnnotation(RequestParam.class);
            if (param == null) {
                continue;
            }

            boolean isRequired = param.required();
            String defaultValue = param.defaultValue();
            String parameterName = param.value();
            Class<?> parameterType = parameter.getParameterType();

            ParamDesc desc = parameter.getParameterAnnotation(ParamDesc.class);
            String range = null;
            if (desc != null) {
                range = desc.range();
            }

            if (!isRequired && parameterType.isPrimitive() && defaultValue == null) {
                log.warn(String.format("controller class [%s]'s method [%s] parameter [%s] is primitive and has not default value. please use RequestParam to mark it is required or use a wrapper class. "
                        , handlerMethod.getBean().getClass().getName(), handlerMethod.getMethod().getName(), parameter.getParameterName()));
            }

            try {
                ParameterValidator validator = new ParameterValidator(parameterName, parameterType, range);
                validators.add(validator);
            } catch (IllegalArgumentException e) {
                log.warn(String.format("init parameter validator error,resource class [%s],method [%s],parameter [%s]"
                        , handlerMethod.getBean().getClass().getName(), handlerMethod.getMethod().getName(), parameter.getParameterName()), e.getMessage());
            } catch (Exception e) {
                log.error(String.format("init parameter validator error,resource class [%s],method [%s],parameter [%s]"
                        , handlerMethod.getBean().getClass().getName(), handlerMethod.getMethod().getName(), parameter.getParameterName()), e.getMessage());
                throw e;
            }
        }

        for (ParameterValidator validator : validators) {
            validator.validate(request.getParameterMap());
        }

        return true;
    }

}
