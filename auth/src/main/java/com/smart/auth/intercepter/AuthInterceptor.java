package com.smart.auth.intercepter;

import com.smart.auth.annotation.BaseInfo;
import com.smart.auth.exception.AuthFailException;
import com.smart.auth.exception.ExcepFactor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chenjunlong
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        BaseInfo baseInfo = handlerMethod.getMethodAnnotation(BaseInfo.class);
        if (baseInfo != null && baseInfo.needAuth()) {
            String source = request.getParameter("source");
            String token = request.getParameter("token");
            if (verifyAuth(source, token)) {
                // set authResponse to RequestThreadLocal
            } else {
                throw new AuthFailException(ExcepFactor.E_AUTH_FAILURE)
                        .setAttribute("source", source)
                        .setAttribute("token", token);
            }
        }
        return true;
    }

    private static final String localToken = "LlpnE4vIbRJTzD7m";
    private boolean verifyAuth(String source, String token) {
        // TODO: verify
        return localToken.equals(token);
    }

}
