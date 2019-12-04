package com.vike.bridge.component;

import com.google.gson.Gson;
import com.vike.bridge.common.CommonResponse;
import com.vike.bridge.config.shiro.AuthUtil;
import com.vike.bridge.dao.SysOperateLogRepository;
import com.vike.bridge.entity.SysOperateLog;
import com.vike.bridge.entity.SysUser;
import com.vike.bridge.utils.IpAddrUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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

    private final static int IP_V6_MARK = 0;
    private static Gson JSON_FORMAT = new Gson();

    @Autowired
    SysOperateLogRepository sysOperateLogRepository;
    /**
     * 定义切入点，切入点为com.vike.bridge.controller下的所有函数
     */
    @Pointcut("execution(public * com.vike.bridge.controller..*.*(..))")
    public void log(){}


    /**记录请求日志*/
    @AfterReturning(returning = "resp", pointcut = "log()")
    public void doAfterReturning(JoinPoint joinPoint, Object resp) throws Throwable {
        SysOperateLog sysOperate = new SysOperateLog();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes!=null){
            HttpServletRequest request = attributes.getRequest();
            String ipStr = IpAddrUtil.ipInRequest(request);
            int ipInt;
            if(ipStr.contains(":")){
                ipInt = IP_V6_MARK;
            }else{
                ipInt=IpAddrUtil.ipToInt(ipStr);
            }
            //登陆方法专门处理
            String requestName = joinPoint.getSignature().toShortString();
            log.info(requestName);
            if("AuthController.login(..)".equals(requestName)||"AuthController.logout()".equals(requestName)||"AuthController.changePsd(..)".equals(requestName)){
                String userName = (String)joinPoint.getArgs()[0];
                sysOperate.setUserId(0L).setUserName(userName).setRequestName(requestName).setRequestParam(userName);
            }else {
                SysUser user = AuthUtil.getUser();
                sysOperate.setUserId(user.getId()).setUserName(user.getName())
                        .setRequestName(requestName).setRequestParam(JSON_FORMAT.toJson(joinPoint.getArgs()));
            }

            CommonResponse response = (CommonResponse)resp;
            sysOperate.setResponseCode(response.getCode()).setResponseMessage(response.getMessage()).setIpAddr(ipInt);
            sysOperateLogRepository.save(sysOperate);
        }else{
            log.error("请求切面实施失败");
        }


    }
}
