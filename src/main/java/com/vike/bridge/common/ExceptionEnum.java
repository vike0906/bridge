package com.vike.bridge.common;

/**
 * @Author: lsl
 * @Date: Create in 2018/12/13
 */
public enum ExceptionEnum {

    SYSTEM_ERROR(-1,"系统异常"),
    TOKEN_ERROR(100,"登陆凭证失效"),

    LOGIN_NAME_UN_EXIST(10001,"用户名不存在"),
    NAME_OR_PASSWORD_ERROR(10002,"用户名或密码错误"),
    USER_STATUS_ERROR(10003,"账户已被冻结"),
    LOGIN_NAME_EXIST(10004,"该登录名已存在"),

    OPTIONAL_ERROR(400,"相关信息不存在"),
    OPTIONAL_DELETE(401,"相关信息不存在"),
    ERROR(500,"unknown error");

    private int code;
    private String message;
    ExceptionEnum(int code, String message){
        this.code = code;
        this.message = message;
    }
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
