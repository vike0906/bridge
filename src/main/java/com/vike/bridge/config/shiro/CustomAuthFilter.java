package com.vike.bridge.config.shiro;

import com.google.gson.Gson;
import com.vike.bridge.common.Assert;
import com.vike.bridge.common.CommonResponse;
import com.vike.bridge.common.ExceptionEnum;
import com.vike.bridge.component.LocalCache;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author: lsl
 * @createDate: 2019/11/26
 */
//@Component
public class CustomAuthFilter extends FormAuthenticationFilter {

    private final static int UN_LOGIN_CODE = 100;
    private final static String UN_LOGIN_MESSAGE = "未登录或登录已失效";

//    /** 使用本地自定义缓存检查登陆凭证 */
//    @Value("${system.loginTimeOut:1800}")
//    private int loginTimeOut;
//    @Override
//    public boolean isAccessAllowed(ServletRequest req, ServletResponse rep, Object mappedValue){
//        boolean allowed = !super.isLoginRequest(req, rep) && super.isPermissive(mappedValue);
//        if (!allowed) {
//            if (req instanceof HttpServletRequest) {
//                HttpServletRequest request = (HttpServletRequest) req;
//
//                if ("OPTIONS".equals(request.getMethod().toUpperCase()))return true;
//
//                String token = request.getHeader("AuthToken");
//                Assert.check(StringUtils.isEmpty(token), ExceptionEnum.ILLEGAL_REQUEST);
//
//                Long timestamp = LocalCache.tokenGet(token);
//                if(timestamp==null){
//                    Assert.failed(ExceptionEnum.TOKEN_ERROR);
//                }
//                Long change = (System.currentTimeMillis()-timestamp)/1000;
//                if(change.intValue()>loginTimeOut){
//                    Assert.failed(ExceptionEnum.TOKEN_ERROR);
//                }
//                return true;
//            }
//        }
//        return allowed;
//    }

    @Override
    public boolean isAccessAllowed(ServletRequest req, ServletResponse rep, Object mappedValue){
        boolean allowed = super.isAccessAllowed(req,rep,mappedValue);
        if (!allowed) {
            if (req instanceof HttpServletRequest) {
                HttpServletRequest request = (HttpServletRequest) req;
                return "OPTIONS".equals(request.getMethod().toUpperCase());
            }
        }
        return allowed;
    }

    /**
     * 在访问controller前判断是否登录，返回json，不进行重定向
     * @param request
     * @param response
     * @return true-继续往下执行，false-该filter过滤器已经处理，不继续执行其他过滤器
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        //这里是个坑，如果不设置的接受的访问源，那么前端都会报跨域错误，因为这里还没到corsConfig里面
        httpServletResponse.setHeader("Access-Control-Allow-Origin", ((HttpServletRequest) request).getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");

        CommonResponse commonResponse = new CommonResponse(UN_LOGIN_CODE, UN_LOGIN_MESSAGE);

        Gson gs = new Gson();

        PrintWriter writer = httpServletResponse.getWriter();
        writer.write(gs.toJson(commonResponse));
        writer.flush();
        return false;
    }
}
