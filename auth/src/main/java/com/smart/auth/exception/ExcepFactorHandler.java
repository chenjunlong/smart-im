package com.smart.auth.exception;

import com.smart.auth.RequestThreadLocal;
import com.smart.auth.domain.BodyResponse;
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
    public BodyResponse apiException(ApiException e) {
        if (RequestThreadLocal.getApiDebug()) {
            return BodyResponse.build(e.getErrorCode(), e.getError(), RequestThreadLocal.getRequestPath(), e.printDetail());
        }
        return BodyResponse.build(e.getErrorCode(), e.getError(), RequestThreadLocal.getRequestPath());
    }

    @ExceptionHandler(AuthFailException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public BodyResponse authException(AuthFailException e) {
        if (RequestThreadLocal.getApiDebug()) {
            return BodyResponse.build(e.getErrorCode(), e.getError(), RequestThreadLocal.getRequestPath(), e.printDetail());
        }
        return BodyResponse.build(e.getErrorCode(), e.getError(), RequestThreadLocal.getRequestPath());
    }
}
