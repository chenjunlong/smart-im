package com.smart.server.common.utils;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * @author chenjunlong
 */
public class NetWorkUtils {

    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface ni = e.nextElement();
                if ("eth0".equals(ni.getName()) || "en0".equals(ni.getName())) {
                    Enumeration<InetAddress> en = ni.getInetAddresses();
                    while (en.hasMoreElements()) {
                        InetAddress ia = en.nextElement();
                        if (ia instanceof Inet6Address) {
                            continue;
                        }
                        return ia.getHostAddress();
                    }
                    break;
                }
            }
        } catch (Exception e) {

        }
        return "";
    }
}
