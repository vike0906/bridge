package com.vike.bridge.component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author: lsl
 * @createDate: 2019/10/25
 */
public class LocalCache {

    private static final Logger log = LoggerFactory.getLogger(LocalCache.class);

    /**构建Token缓存容器,30分钟后过期*/
    private static LoadingCache<Long, String> TOKEN_CACHE = CacheBuilder.newBuilder()
            .initialCapacity(100)
            .concurrencyLevel(10)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build(new CacheLoader<Long, String>() {
                @Override
                public String load(Long s){
                    return null;
                }
            });

    /**储存Token*/
    public static void putToken(Long key, String value){
        TOKEN_CACHE.put(key,value);
    }

    /**获取Token*/
    public static String getToken(Long key){
        return TOKEN_CACHE.getIfPresent(key);
    }

    /**移除Token*/
    public static void removeToken(Long key){
        TOKEN_CACHE.invalidate(key);
        TOKEN_CACHE.cleanUp();
    }

    /**构建验证码缓存容器,5分钟后过期*/
    private static LoadingCache<String, String> CAPTCHA_CACHE = CacheBuilder.newBuilder()
            .initialCapacity(200)
            .concurrencyLevel(10)
            .expireAfterWrite(300, TimeUnit.SECONDS)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String s){
                    return null;
                }
            });

    /**储存Captcha*/
    public static void putCaptcha(String key, String value){
        CAPTCHA_CACHE.put(key,value);
    }

    /**获取Captcha*/
    public static String getCaptcha(String key){
        return CAPTCHA_CACHE.getIfPresent(key);
    }

}
