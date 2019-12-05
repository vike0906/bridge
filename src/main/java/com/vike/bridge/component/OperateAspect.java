package com.vike.bridge.component;

import com.google.gson.Gson;
import com.vike.bridge.common.ApiPointcut;
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


    private static Gson JSON_FORMAT = new Gson();

    @Autowired
    SysOperateLogRepository sysOperateLogRepository;

    /**登陆请求*/
    @Around("execution( * login(..))&&@annotation(apiPointcut)")
    public Object aroundLogin(ProceedingJoinPoint joinPoint, ApiPointcut apiPointcut){
        Object proceed = null;
        try{
            proceed = joinPoint.proceed();

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if(attributes!=null && (proceed instanceof CommonResponse)){

                SysOperateLog sysOperate = new SysOperateLog();
                HttpServletRequest request = attributes.getRequest();
                String ipStr = IpAddrUtil.ipInRequest(request);
                int ipInt = IpAddrUtil.ipToInt(ipStr);

                SysUser user = AuthUtil.getUser();
                sysOperate.setUserId(user.getId()).setUserName(user.getName())
                        .setRequestName(apiPointcut.value()).setRequestParam("*");

                CommonResponse response = (CommonResponse)proceed;
                sysOperate.setResponseCode(response.getCode()).setResponseMessage(response.getMessage()).setIpAddr(ipInt);
                sysOperateLogRepository.save(sysOperate);
            }else{
                log.error("System Login Aspect Exception...");
            }
        }catch (Exception e){
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return proceed;
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
                    .setRequestName(apiPointcut.value())
                    .setRequestParam("*").setIpAddr(ipInt);
        }else{
            log.error("System Logout Or ChangePsd Aspect Exception...");
        }
        Object proceed = null;
        try{

            proceed = joinPoint.proceed();

            if(proceed instanceof CommonResponse){

                CommonResponse response = (CommonResponse)proceed;
                sysOperate.setResponseCode(response.getCode()).setResponseMessage(response.getMessage());

            }else{
                log.error("System Logout Or ChangePsd Aspect Exception...");
            }
            sysOperateLogRepository.save(sysOperate);
        }catch (Exception e){
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return proceed;
    }

    /**记录请求日志*/
    @AfterReturning(returning = "resp", pointcut = "execution(public * com.vike.bridge.controller..*.*(..))&&!target(com.vike.bridge.controller.AuthController)&&@annotation(apiPointcut)")
    public void doAfterReturning(JoinPoint joinPoint, Object resp,ApiPointcut apiPointcut) throws Throwable {
        SysOperateLog sysOperate = new SysOperateLog();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes!=null&&resp instanceof CommonResponse){

            HttpServletRequest request = attributes.getRequest();
            String ipStr = IpAddrUtil.ipInRequest(request);
            int ipInt = IpAddrUtil.ipToInt(ipStr);

            SysUser user = AuthUtil.getUser();
            sysOperate.setUserId(user.getId()).setUserName(user.getName())
                    .setRequestName(apiPointcut.value()).setRequestParam(JSON_FORMAT.toJson(joinPoint.getArgs()));

            CommonResponse response = (CommonResponse)resp;
            sysOperate.setResponseCode(response.getCode()).setResponseMessage(response.getMessage()).setIpAddr(ipInt);
            sysOperateLogRepository.save(sysOperate);
        }else{
            log.error("System API Aspect Exception...");
        }


    }
}
