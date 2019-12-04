package com.vike.bridge.component;


import com.google.gson.Gson;
import com.vike.bridge.common.CommonResponse;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author: lsl
 * @createDate: 2019/11/28
 */
@Component
public class CorsFilter implements Filter {

    private final String OPTIONS_MESSAGE = "Options request is allowed";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        if(servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse){
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            HttpServletRequest request = (HttpServletRequest)servletRequest;
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, AuthToken");
            if("OPTIONS".equals(request.getMethod().toUpperCase())){
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json");
                CommonResponse commonResponse = CommonResponse.success(OPTIONS_MESSAGE);
                Gson gs = new Gson();
                PrintWriter writer = response.getWriter();
                writer.write(gs.toJson(commonResponse));
                writer.flush();
            }else{
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }else {
            throw new ServletException("不支持的请求类型");
        }
    }

    @Override
    public void destroy() {

    }

}
