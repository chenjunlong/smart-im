package com.smart.api.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

/**
 * @author chenjunlong
 */
@Component
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final Logger requestLog = LoggerFactory.getLogger("request");
    private static final String RID = "rid";
    private static final String STIME = "stime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        MDC.put(RID, UUID.randomUUID().toString().replace("-", ""));
        MDC.put(STIME, String.valueOf(System.currentTimeMillis()));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // 含义：接口名 | 接口类型 | 响应状态 | 响应时间 | uid | 参数 | 用户ip
        String path = request.getRequestURI();
        String method = request.getMethod();
        int status = response.getStatus();
        long cost = System.currentTimeMillis() - Long.parseLong(MDC.get("stime"));
        long uid = RequestThreadLocal.getLoginUid();
        String params = getParams(request);
        String clientIp = getClientIp(request);

        requestLog.info(String.format("%s %s %s %s %s %s %s", path, method, status, cost, uid, params, clientIp));
        removeMDC();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }

    private void removeMDC() {
        MDC.remove(RID);
        MDC.remove(STIME);
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
}
