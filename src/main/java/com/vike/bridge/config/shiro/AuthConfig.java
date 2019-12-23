package com.vike.bridge.config.shiro;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: lsl
 * @createDate: 2019/3/13
 */
@Configuration
public class AuthConfig {

    @Value("${system.loginTimeOut:1800}")
    private int loginTimeOut;

    protected final static String ALGORITHM_NAME = "SHA-256";
    protected final static int ITERATIONS = 10;

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        filters.put("authc",new CustomAuthFilter());
        shiroFilterFactoryBean.setSecurityManager(securityManager);


        Map<String,String> map = new LinkedHashMap<>();

        map.put("/test", "anon");
        map.put("/login", "anon");
        map.put("/captcha", "anon");
        map.put("/validation", "anon");
        map.put("/swagger-ui.html", "anon");
        map.put("/v2/api-docs", "anon");
        map.put("/webjars/springfox-swagger-ui/**", "anon");
        map.put("/swagger-resources/**", "anon");
        map.put("/webSocket/**","anon");

        map.put("/static/**", "anon");
        map.put("/**/*.png", "anon");
        map.put("/**/*.jpg", "anon");
        map.put("/**/*.jpg", "anon");
        map.put("/**/*.jpeg", "anon");
        map.put("/**/*.css", "anon");
        map.put("/**/*.js", "anon");
        map.put("/**/*.html", "anon");
        map.put("/**", "authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }

    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName(ALGORITHM_NAME);
        hashedCredentialsMatcher.setHashIterations(ITERATIONS);
        return hashedCredentialsMatcher;
    }

    @Bean
    public CustomAuthRealm customAuthRealm() {
        CustomAuthRealm customAuthRealm = new CustomAuthRealm();
        customAuthRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return customAuthRealm;
    }

    @Bean
    public MemorySessionDAO memorySessionDAO(){
        MemorySessionDAO memorySessionDAO = new MemorySessionDAO();
        memorySessionDAO.setSessionIdGenerator(new CustomSessionIdGenerator());
        return memorySessionDAO;
    }

    @Bean
    public CustomAuthSessionManager getDefaultWebSessionManager() {
        CustomAuthSessionManager customAuthSessionManager = new CustomAuthSessionManager();
        customAuthSessionManager.setSessionDAO(memorySessionDAO());
        customAuthSessionManager.setGlobalSessionTimeout(1000 * loginTimeOut);
        return customAuthSessionManager;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSessionManager(getDefaultWebSessionManager());
        securityManager.setRealm(customAuthRealm());
        return securityManager;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }





}
