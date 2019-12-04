package com.vike.bridge.common;

/**
 * @author: lsl
 * @createDate: 2019/11/26
 */
public class Assert {

    public static void failed(ExceptionEnum e) {
        throw new BusinessException(e.getCode(), e.getMessage());
    }

    public static void failed(String message) {
        throw new BusinessException(CommonResponse.failCode, message);
    }

    public static void check(boolean check, ExceptionEnum e){
        if (check){
            throw new BusinessException(e.getCode(), e.getMessage());
        }
    }

    public static void check(boolean check, int code, String msg){
        if (check){
            throw new BusinessException(code, msg);
        }
    }
}
