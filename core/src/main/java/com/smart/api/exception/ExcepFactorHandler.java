package com.smart.api.exception;

import com.smart.api.auth.AuthFailException;
import com.smart.api.domain.ApiResponse;
import com.smart.api.request.RequestThreadLocal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author chenjunlong
 */
@ControllerAdvice
public class ExcepFactorHandler {

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiResponse apiException(ApiException e) {
        if (RequestThreadLocal.getApiDebug()) {
            return ApiResponse.failure(e.getErrorCode(), e.getError(), RequestThreadLocal.getRequestPath(), e.getAttributes());
        }
        return ApiResponse.failure(e.getErrorCode(), e.getError(), RequestThreadLocal.getRequestPath());
    }

    @ExceptionHandler(AuthFailException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ApiResponse authException(AuthFailException e) {
        if (RequestThreadLocal.getApiDebug()) {
            return ApiResponse.failure(e.getErrorCode(), e.getError(), RequestThreadLocal.getRequestPath(), e.getAttributes());
        }
        return ApiResponse.failure(e.getErrorCode(), e.getError(), RequestThreadLocal.getRequestPath());
    }
}
