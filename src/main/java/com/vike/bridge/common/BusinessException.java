package com.vike.bridge.common;

/**
 * @author: lsl
 * @createDate: 2019/11/6
 */
public class BusinessException extends RuntimeException {

    private int code;

    public BusinessException(int code){
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
