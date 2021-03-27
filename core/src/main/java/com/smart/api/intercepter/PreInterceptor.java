package com.smart.api.intercepter;

import com.smart.api.request.RequestThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chenjunlong
 */
@Component
public class PreInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        RequestThreadLocal.setRequest(request);
        RequestThreadLocal.setRequestPath(request.getRequestURI());
        setDebug(request);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        RequestThreadLocal.clear();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }

    private void setDebug(HttpServletRequest request) {
        String debug = request.getParameter("debug");
        try {
            RequestThreadLocal.setApiDebug(debug == null ? false : Boolean.parseBoolean(debug));
        } catch (Exception e) {
            RequestThreadLocal.setApiDebug(Boolean.FALSE);
        }
    }

}
