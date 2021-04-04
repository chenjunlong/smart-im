package com.smart.api.exception;

import com.smart.api.domain.ApiResponse;
import com.smart.api.intercepter.request.RequestThreadLocal;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenjunlong
 */
@RestController
public class ApiNotFoundException implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
    @ResponseBody
    public ApiResponse error() {
        return ApiResponse.failure(ExcepFactor.E_NOTFOUND_API.getErrorCode(), ExcepFactor.E_NOTFOUND_API.getError(), RequestThreadLocal.getRequestPath());
    }
}
