package com.smart.api.request;

import javax.servlet.http.HttpServletRequest;

/**
 * @author chenjunlong
 */
public class RequestThreadLocal {

    private static ThreadLocal<HttpServletRequest> httpRequest = new ThreadLocal<>();
    // 接口路径
    private static ThreadLocal<String> requestPath = new ThreadLocal<>();
    // 调试参数true打开调试 false关闭调试
    private static ThreadLocal<Boolean> apiDebug = new ThreadLocal<>();
    // 登陆用户uid
    private static ThreadLocal<Long> loginUid = new ThreadLocal<>();

    public static void setRequestPath(String path) {
        requestPath.set(path);
    }

    public static String getRequestPath() {
        return requestPath.get();
    }

    public static void setApiDebug(boolean debug) {
        apiDebug.set(debug);
    }

    public static boolean getApiDebug() {
        return apiDebug.get();
    }

    public static void setRequest(HttpServletRequest request) {
        httpRequest.set(request);
    }

    public static HttpServletRequest getRequest() {
        return httpRequest.get();
    }

    public static void setLoginUid(long uid) {
        loginUid.set(uid);
    }

    public static long getLoginUid() {
        return loginUid.get() == null ? 0L : loginUid.get();
    }

    public static void clear() {
        httpRequest.remove();
        requestPath.remove();
        apiDebug.remove();
        loginUid.remove();
    }
}
