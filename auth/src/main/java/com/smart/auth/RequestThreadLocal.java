package com.smart.auth;

/**
 * @author chenjunlong
 */
public class RequestThreadLocal {

    private static ThreadLocal<String> requestPath = new ThreadLocal<>();
    private static ThreadLocal<Boolean> apiDebug = new ThreadLocal<>();

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

    public static void clear() {
        requestPath.remove();
        apiDebug.remove();
    }
}
