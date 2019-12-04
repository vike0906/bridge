package com.vike.bridge.config.shiro;

import com.vike.bridge.common.ExceptionEnum;
import com.vike.bridge.common.GlobalConstant;
import com.vike.bridge.dao.SysUserRepository;
import com.vike.bridge.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * @author: lsl
 * @createDate: 2019/3/13
 */
@Slf4j
public class CustomAuthRealm extends AuthorizingRealm {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SysUser user = (SysUser)principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addRole(String.valueOf(user.getRole().getCode()));
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        if (authenticationToken.getPrincipal() == null) {
            return null;
        }
        String name = authenticationToken.getPrincipal().toString();

        Optional<SysUser> op = sysUserRepository.findSysUserByLoginName(name);
        if(!op.isPresent()){
            throw new AuthenticationException(ExceptionEnum.LOGIN_NAME_UN_EXIST.getMessage());
        }
        SysUser user = op.get();
        if(GlobalConstant.ABNORMAL==user.getStatus()){
            throw new AuthenticationException(ExceptionEnum.USER_STATUS_ERROR.getMessage());
        }
        if(GlobalConstant.ABNORMAL==user.getRole().getStatus()){
            throw new AuthenticationException(ExceptionEnum.USER_STATUS_ERROR.getMessage());
        }

        ByteSource salt = ByteSource.Util.bytes(user.getSalt());

        return new SimpleAuthenticationInfo(user, user.getPassword(),salt, getName());
    }
}
