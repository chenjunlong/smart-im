package com.smart.auth.intercepter;

import com.smart.auth.RequestThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

/**
 * @author chenjunlong
 */
@Component
public class PreInterceptor implements WebRequestInterceptor {


    @Override
    public void preHandle(WebRequest request) {
        RequestThreadLocal.setRequestPath(request.getContextPath());
        setDebug(request);
    }

    private void setDebug(WebRequest request) {
        String debug = request.getParameter("debug");
        try {
            RequestThreadLocal.setApiDebug(debug == null ? false : Boolean.parseBoolean(debug));
        } catch (Exception e) {
            RequestThreadLocal.setApiDebug(Boolean.FALSE);
        }
    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) {
        RequestThreadLocal.clear();
    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) {

    }
}
