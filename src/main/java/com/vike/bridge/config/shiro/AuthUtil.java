package com.vike.bridge.config.shiro;

import com.vike.bridge.component.LocalCache;
import com.vike.bridge.entity.SysUser;
import com.vike.bridge.utils.EncryptUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * @author: lsl
 * @createDate: 2019/3/14
 */
public class AuthUtil {

    /**
     * 获取SysUser实例
     */
    public static SysUser getUser(){
        return (SysUser) SecurityUtils.getSubject().getPrincipal();
    }

    /**
     * 计算加密后的密文
     */
    public static String hash(String psd, String salt){
        SimpleHash simpleHash = new SimpleHash(AuthConfig.ALGORITHM_NAME, psd, salt,AuthConfig.ITERATIONS);
        return simpleHash.toString();
    }

    /**
     * 清除已登陆用户
     */
    public static void remove(Long userId){
        String token1 = LocalCache.getToken(userId);
        if(!StringUtils.isEmpty(token1)){
            AuthUtil.remove(token1);
        }
    }

    public static void remove(String token){
        DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager)SecurityUtils.getSecurityManager();
        CustomAuthSessionManager sessionManager = (CustomAuthSessionManager)securityManager.getSessionManager();
        SessionDAO sessionDAO = sessionManager.getSessionDAO();
        Collection<Session> activeSessions = sessionDAO.getActiveSessions();
        for(Session session:activeSessions){
            if(token.equals(session.getId().toString())){
                sessionDAO.delete(session);
            }
        }
    }

    public static void main(String [] args){
        System.out.println(hash("123456","salt"));
    }


}
