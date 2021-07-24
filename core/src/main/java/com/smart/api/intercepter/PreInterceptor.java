package com.smart.api.intercepter;

import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.google.common.collect.Maps;
import com.smart.api.intercepter.constant.Constant;
import com.smart.api.intercepter.request.RequestThreadLocal;

/**
 * @author chenjunlong
 */
@Component
public class PreInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        Map<String, String> map = Maps.newHashMap();
        map.put(Constant.RID, UUID.randomUUID().toString().replace("-", ""));
        map.put(Constant.STIME, String.valueOf(System.currentTimeMillis()));
        MDC.setContextMap(map);

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
        MDC.clear();
    }
}
