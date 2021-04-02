package com.smart.api.intercepter;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.smart.api.intercepter.constant.Constant;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.smart.api.intercepter.request.RequestThreadLocal;

/**
 * @author chenjunlong
 */
@Component
public class PreInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        MDC.put(Constant.RID, UUID.randomUUID().toString().replace("-", ""));
        MDC.put(Constant.STIME, String.valueOf(System.currentTimeMillis()));

        RequestThreadLocal.setRequest(request);
        RequestThreadLocal.setRequestPath(request.getRequestURI());
        setDebug(request);

        return true;
    }

    private void setDebug(HttpServletRequest request) {
        String debug = request.getParameter("debug");
        try {
            RequestThreadLocal.setApiDebug(debug == null ? false : Boolean.parseBoolean(debug));
        } catch (Exception e) {
            RequestThreadLocal.setApiDebug(Boolean.FALSE);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        removeMDC();
    }

    private void removeMDC() {
        MDC.remove(Constant.RID);
        MDC.remove(Constant.STIME);
    }
}
