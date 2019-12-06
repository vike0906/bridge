package com.vike.bridge.controller;

import com.vike.bridge.common.*;
import com.vike.bridge.component.LocalCache;
import com.vike.bridge.dao.SysUserRepository;
import com.vike.bridge.entity.SysUser;
import com.vike.bridge.utils.CaptchaUtil;
import com.vike.bridge.utils.RandomUtil;
import com.vike.bridge.config.shiro.AuthUtil;
import com.vike.bridge.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * @author: lsl
 * @createDate: 2019/11/27
 */
@Slf4j
@RestController
public class AuthController {

    @Autowired
    SysUserRepository sysUserRepository;

    @ApiPointcut("登陆系统")
    @PostMapping("login")
    public CommonResponse<UserVo> login(@RequestParam String name, @RequestParam String password){

        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(name.trim(), password.trim());

        try {
            subject.login(usernamePasswordToken);
        }catch (AuthenticationException e){
            log.error(e.getMessage());
            if(ExceptionEnum.USER_STATUS_ERROR.getMessage().equals(e.getMessage())){
                Assert.failed(ExceptionEnum.USER_STATUS_ERROR);
            }
            Assert.failed(ExceptionEnum.NAME_OR_PASSWORD_ERROR);
        }

        String token = subject.getSession().getId().toString();

        SysUser user = AuthUtil.getUser();

        /** 存在已登陆信息，则剔除已登录*/
        String token1 = LocalCache.getToken(user.getId());
        if(!StringUtils.isEmpty(token1)){
            AuthUtil.remove(token1);
        }

        LocalCache.putToken(user.getId(),token);

        UserVo vo = new UserVo(user.getName(),token);

        log.info("用户：{} 登录成功",user.getName());

        return new CommonResponse<>(vo);
    }

    @ApiPointcut("退出登陆")
    @PostMapping("logout")
    public CommonResponse logout(){
        SysUser user = AuthUtil.getUser();
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        LocalCache.removeToken(user.getId());
        log.info("用户：{}退出登录",user.getName());
        return CommonResponse.success("当前登录已注销");
    }

    @ApiPointcut("修改密码")
    @PostMapping("change-psd")
    public CommonResponse changePsd(@RequestParam String oldPsd, @RequestParam String newPsd){

        SysUser user = AuthUtil.getUser();

        String hash1 = AuthUtil.hash(oldPsd, user.getSalt());

        if(hash1.equals(user.getPassword())){

            String salt = RandomUtil.randomString(GlobalConstant.SALT_LENGTH);
            String hash2 = AuthUtil.hash(newPsd, salt);

            Optional<SysUser> op = sysUserRepository.findById(user.getId());
            Assert.check(!op.isPresent(),ExceptionEnum.SYSTEM_ERROR);

            SysUser sysUser = op.get();
            sysUser.setPassword(hash2).setSalt(salt);

            sysUserRepository.save(sysUser);

            logout();

            return CommonResponse.success("密码修改成功,请重新登陆");
        }else {
            return CommonResponse.fail("密码错误");
        }
    }

    @GetMapping("captcha")
    public void captcha(HttpServletResponse response, @RequestParam String serial){

        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");

        try {

            String captcha = CaptchaUtil.generateCaptcha(response.getOutputStream());

            LocalCache.putCaptcha(serial,captcha.toLowerCase());

        } catch (IOException e) {

            log.error("获取验证码图片并回传失败：{}",serial);

        }
    }

    @PostMapping("validation")
    public CommonResponse validation(@RequestParam String serial, @RequestParam String captcha){

        String captcha1 = LocalCache.getCaptcha(serial);

        if(captcha.toLowerCase().equals(captcha1)){
            return CommonResponse.success();
        }else{
            return CommonResponse.fail("图片验证码校验失败");
        }
    }
}
