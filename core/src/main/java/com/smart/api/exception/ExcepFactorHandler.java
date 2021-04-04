package com.smart.api.exception;

import com.smart.api.domain.ApiResponse;
import com.smart.api.intercepter.request.RequestThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author chenjunlong
 */
@Slf4j
@ControllerAdvice
public class ExcepFactorHandler {

    /**
     * 参数错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler(ApiInvalidParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResponse apiInvalidParameterException(ApiInvalidParameterException e) {
        if (RequestThreadLocal.getApiDebug()) {
            return ApiResponse.failure(e.getErrorCode(), e.getError(), RequestThreadLocal.getRequestPath(), e.getAttributes());
        }
        return ApiResponse.failure(e.getErrorCode(), e.getError(), RequestThreadLocal.getRequestPath());
    }

    /**
     * 认证失败
     * 
     * @param e
     * @return
     */
    @ExceptionHandler(AuthFailException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ApiResponse authException(AuthFailException e) {
        if (RequestThreadLocal.getApiDebug()) {
            return ApiResponse.failure(e.getErrorCode(), e.getError(), RequestThreadLocal.getRequestPath(), e.getAttributes());
        }
        return ApiResponse.failure(e.getErrorCode(), e.getError(), RequestThreadLocal.getRequestPath());
    }

    /**
     * 服务器内部错误
     * 
     * @param e
     * @return
     */
    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiResponse apiException(ApiException e) {
        if (RequestThreadLocal.getApiDebug()) {
            return ApiResponse.failure(e.getErrorCode(), e.getError(), RequestThreadLocal.getRequestPath(), e.getAttributes());
        }
        return ApiResponse.failure(e.getErrorCode(), e.getError(), RequestThreadLocal.getRequestPath());
    }

    /**
     * 服务器内部错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler({RuntimeException.class, Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiResponse exception(Exception e) {
        log.error(e.getMessage(), e);
        return ApiResponse.failure(ExcepFactor.E_DEFAULT.getErrorCode(), ExcepFactor.E_DEFAULT.getError(), RequestThreadLocal.getRequestPath());
    }
}
