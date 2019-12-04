package com.vike.bridge.config.shiro;

import com.vike.bridge.utils.RandomUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import java.io.Serializable;

/**
 * @author: lsl
 * @createDate: 2019/11/27
 */
public class CustomSessionIdGenerator implements SessionIdGenerator {
    @Override
    public Serializable generateId(Session session) {
        return RandomUtil.UUID();
    }
}
