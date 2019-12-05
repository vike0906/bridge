package com.vike.bridge.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: lsl
 * @createDate: 2019/12/4
 */
public class IpAddrUtil {

    /**IP分割符*/
    private final static String SPLIT = ".";
    /**IP6标识*/
    private final static int IP_V6_MARK = 0;

    /**获取IP地址*/
    public static String ipInRequest(HttpServletRequest request){

        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


    public static int ipToInt(String idAddr) {
        if(idAddr.contains(":")){
            return IP_V6_MARK;
        }
        String[] ips = idAddr.split("\\.");
        int ipInt = 0;
        for (int i = 0; i < ips.length; i++) {
            ipInt <<= 8;
            ipInt ^= (byte) Integer.valueOf(ips[i]).intValue() & 255;
        }
        return ipInt;
    }


    public static String intToIp(int ipInt) {
        int[] ipArr = new int[4];
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            ipArr[3 - i] ^= (byte) ipInt & 255;
            ipInt >>>= 8;
            sb.insert(0,ipArr[3 - i]);
            sb.insert(0,SPLIT);
        }
        sb.deleteCharAt(0);
        return sb.toString();
    }

    //test
    public static void main(String[] args) {

        int ipInt = ipToInt("1.1.1.2");
        System.out.println("ipInt:" + ipInt);
        String ipArr = intToIp(2005991947);
        System.out.println(ipArr);

    }


}
