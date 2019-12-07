package com.vike.bridge.component;

import com.google.gson.Gson;
import com.vike.bridge.common.ApiPointcut;
import com.vike.bridge.common.BusinessException;
import com.vike.bridge.common.CommonResponse;
import com.vike.bridge.config.shiro.AuthUtil;
import com.vike.bridge.dao.SysOperateLogRepository;
import com.vike.bridge.entity.SysOperateLog;
import com.vike.bridge.entity.SysUser;
import com.vike.bridge.utils.IpAddrUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: lsl
 * @createDate: 2019/12/4
 */
@Slf4j
@Aspect
@Component
public class OperateAspect {

    /** 登陆请求*/
    public final static int LOGIN_REQUEST = 1;

    /** 修改密码*/
    public final static int CHANGE_PSD_REQUEST = 2;

    /** 退出登陆*/
    public final static int LOGOUT_REQUEST = 3;

    /** 普通请求*/
    public final static int COMMON_REQUEST = 4;

    private static Gson JSON_FORMAT = new Gson();

    @Autowired
    SysOperateLogRepository sysOperateLogRepository;

    /**登陆请求*/
    @Around("execution( * login(..))&&@annotation(apiPointcut)")
    public Object aroundLogin(ProceedingJoinPoint joinPoint, ApiPointcut apiPointcut){
        try{
            Object proceed = joinPoint.proceed();

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if(attributes!=null && (proceed instanceof CommonResponse)){

                SysOperateLog sysOperate = new SysOperateLog();
                HttpServletRequest request = attributes.getRequest();
                String ipStr = IpAddrUtil.ipInRequest(request);
                int ipInt = IpAddrUtil.ipToInt(ipStr);

                SysUser user = AuthUtil.getUser();
                sysOperate.setUserId(user.getId()).setUserName(user.getName())
                        .setRequestType(LOGIN_REQUEST)
                        .setRequestName(apiPointcut.value()).setRequestParam("*");

                CommonResponse response = (CommonResponse)proceed;
                sysOperate.setResponseCode(response.getCode()).setResponseMessage(response.getMessage()).setIpAddr(ipInt);
                sysOperateLogRepository.save(sysOperate);
            }else{
                log.error("System Login Aspect Exception...");
            }
            return proceed;
        }catch (Throwable throwable) {
            throw new BusinessException(throwable.getMessage());
        }

    }

    /**修改密码或退出登陆*/
    @Around("(execution( * changePsd(..))|| execution(* logout()))&&@annotation(apiPointcut)")
    public Object aroundLogoutOrChangePsd(ProceedingJoinPoint joinPoint, ApiPointcut apiPointcut){

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        SysOperateLog sysOperate = new SysOperateLog();

        if(attributes!=null){

            HttpServletRequest request = attributes.getRequest();
            String ipStr = IpAddrUtil.ipInRequest(request);
            int ipInt = IpAddrUtil.ipToInt(ipStr);

            SysUser user = AuthUtil.getUser();

            sysOperate.setUserId(user.getId()).setUserName(user.getName())
                    .setRequestType("修改密码".equals(apiPointcut.value())?CHANGE_PSD_REQUEST:LOGOUT_REQUEST)
                    .setRequestName(apiPointcut.value()).setRequestParam("*").setIpAddr(ipInt);
        }else{
            log.error("System Logout Or ChangePsd Aspect Exception...");
        }
        try{

            Object proceed = joinPoint.proceed();

            if(proceed instanceof CommonResponse){

                CommonResponse response = (CommonResponse)proceed;
                sysOperate.setResponseCode(response.getCode()).setResponseMessage(response.getMessage());

            }else{
                log.error("System Logout Or ChangePsd Aspect Exception...");
            }

            sysOperateLogRepository.save(sysOperate);

            return proceed;
        } catch (Throwable throwable) {
            throw new BusinessException(throwable.getMessage());
        }

    }

    /**记录普通请求日志*/
    @Around("execution(public * com.vike.bridge.controller..*.*(..))&&!target(com.vike.bridge.controller.AuthController)&&@annotation(apiPointcut)")
    public Object commonRequest(ProceedingJoinPoint joinPoint, ApiPointcut apiPointcut){

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        SysOperateLog sysOperate = new SysOperateLog();

        if(attributes!=null){

            HttpServletRequest request = attributes.getRequest();
            String ipStr = IpAddrUtil.ipInRequest(request);
            int ipInt = IpAddrUtil.ipToInt(ipStr);

            SysUser user = AuthUtil.getUser();
            sysOperate.setUserId(user.getId()).setUserName(user.getName()).setIpAddr(ipInt)
                    .setRequestType(COMMON_REQUEST)
                    .setRequestName(apiPointcut.value())
                    .setRequestParam(JSON_FORMAT.toJson(joinPoint.getArgs()));
        }else{
            log.error("System API Aspect Exception...");
        }

        try{

            Object proceed = joinPoint.proceed();

            if(proceed instanceof CommonResponse){

                CommonResponse response = (CommonResponse)proceed;
                sysOperate.setResponseCode(response.getCode()).setResponseMessage(response.getMessage());
                sysOperateLogRepository.save(sysOperate);

            }else{
                log.error("System API Aspect Exception...");
            }

            return proceed;
        } catch (Throwable throwable) {

            sysOperate.setResponseCode(200).setResponseMessage(throwable.getMessage());
            sysOperateLogRepository.save(sysOperate);

            throw new BusinessException(throwable.getMessage());
        }

    }
}
