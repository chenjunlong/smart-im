package com.smart.api.intercepter.advice;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.smart.api.domain.ApiResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author chenjunlong
 */
@ControllerAdvice
public class ApiResponseBodyAdvice implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest,
            ServerHttpResponse serverHttpResponse) {

        ServletServerHttpRequest req = (ServletServerHttpRequest) serverHttpRequest;
        HttpServletRequest servletRequest = req.getServletRequest();
        servletRequest.setAttribute("body", body);

        if (body instanceof ApiResponse) {
            return body;
        }
        return ApiResponse.success(body);
    }
}
