package com.smart.api.intercepter.request;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.google.gson.Gson;
import com.smart.api.annotation.RequestLog;
import com.smart.api.annotation.ResponseLog;
import com.smart.api.intercepter.constant.Constant;

/**
 * @author chenjunlong
 */
@Component
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final Gson gson = new Gson();
    private static final Logger requestLog = LoggerFactory.getLogger("request");
    private static final Logger responseLog = LoggerFactory.getLogger("response");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequestLog reqLog = handlerMethod.getMethodAnnotation(RequestLog.class);
        if (reqLog != null) {
            // 含义：接口名 | 接口类型 | 响应状态 | 响应时间 | uid | 参数 | 用户ip
            String path = request.getRequestURI();
            String method = request.getMethod();
            int status = response.getStatus();
            long cost = System.currentTimeMillis() - Long.parseLong(MDC.get(Constant.STIME));
            long uid = RequestThreadLocal.getLoginUid();
            String params = getParams(request);
            String clientIp = getClientIp(request);

            requestLog.info(String.format("%s %s %s %s %s %s %s", path, method, status, cost, uid, params, clientIp));
        }
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.equals("") && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.equals("") && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    private String getParams(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        if (MapUtils.isEmpty(params)) {
            return null;
        }

        String queryString = "";
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                queryString += key + "=" + value + "&";
            }
        }
        // 去掉最后一个空格
        return queryString.substring(0, queryString.length() - 1);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        ResponseLog resLog = handlerMethod.getMethodAnnotation(ResponseLog.class);
        if (resLog != null) {
            // 含义：接口名 | 接口类型 | 响应状态 | 响应时间 | uid | 参数 | 用户ip
            String path = request.getRequestURI();
            String method = request.getMethod();
            int status = response.getStatus();
            long cost = System.currentTimeMillis() - Long.parseLong(MDC.get(Constant.STIME));
            long uid = RequestThreadLocal.getLoginUid();
            String body = request.getAttribute("body") == null ? null : gson.toJson(request.getAttribute("body"));
            String clientIp = getClientIp(request);
            responseLog.info(String.format("%s %s %s %s %s %s %s", path, method, status, cost, uid, body, clientIp));
        }
    }

}
