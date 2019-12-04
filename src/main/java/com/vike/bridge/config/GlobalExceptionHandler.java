package com.vike.bridge.config;

import com.vike.bridge.common.BusinessException;
import com.vike.bridge.common.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author: lsl
 * @createDate: 2019/3/26
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthorizationException.class)
    public CommonResponse authorization(AuthorizationException authorizationException){
        log.error("认证异常：{}",authorizationException.getMessage());
        return handler(authorizationException);
    }

    @ExceptionHandler(BusinessException.class)
    public CommonResponse exception(BusinessException e){
        log.error("业务异常：{}",e.getMessage());
        return handler(e);
    }

    @ExceptionHandler(Exception.class)
    public CommonResponse exception(Exception e){
        log.error("系统异常：{}",e.getMessage());
        return handler(e);
    }

    private CommonResponse handler(Exception e){
        e.printStackTrace();
        return CommonResponse.fail(e.getMessage());
    }
}
