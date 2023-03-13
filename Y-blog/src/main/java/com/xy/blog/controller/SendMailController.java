package com.xy.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xy.blog.entity.SysUser;
import com.xy.blog.mapper.SysUserMapper;
import com.xy.blog.service.BlogLoginService;
import com.xy.blog.service.SendMailService;
import com.xy.blog.service.impl.SendMailServiceImpl;
import com.xy.blog.utils.MailUtils;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.vo.CaptchaVo;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
public class SendMailController {

    @Autowired
    private SendMailServiceImpl sendMailService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @GetMapping("/api/mail/sendCaptcha")
    public ResponseResult sendMail(@RequestParam("email") String email, HttpServletRequest request) throws MessagingException, IOException {
        String message = MailUtils.sendMail(email);
        System.out.println("////////////" + message);
        String verifyCode = message.substring(6, 12);
        System.out.println("!!!!!!!!!!!!!!!!!!!" + verifyCode);
       /* session.setAttribute("verifyCode", verifyCode);
        String verifyCode1 =(String) session.getAttribute("verifyCode");*/
        request.getServletContext().setAttribute("verifyCode",verifyCode);
        return ResponseResult.okResult();
    }

    @PostMapping("/api/user/login/email")
    public ResponseResult login(@RequestBody CaptchaVo captchaVo,HttpServletRequest request ) {
        String captcha = captchaVo.getCaptcha();
        String email = captchaVo.getEmail();
        //String verifyCode =(String) session.getAttribute("verifyCode")
        String verifyCode =(String) request.getServletContext().getAttribute("verifyCode");
        System.out.println("---------------------------"+verifyCode);
        //验证码输入正确
        if (captcha.equals(verifyCode)) {
            //将userInfo和token返回给前端
            return sendMailService.loginByEmail(email);
        } else {
            return ResponseResult.okResult();
        }

    }

    @GetMapping("/api/mail/sendVerifyMailCaptcha")
        public ResponseResult sendVerifyMailCaptcha(@RequestParam("email") String email,HttpServletRequest httpServletRequest) throws MessagingException, IOException {
        String message = MailUtils.sendMail(email);
        System.out.println("////////////" + message);
        String verifyCode = message.substring(6, 12);
        System.out.println("!!!!!!!!!!!!!!!!!!!" + verifyCode);
        httpServletRequest.getServletContext().setAttribute("verifyCode", verifyCode);
        return ResponseResult.okResult();
    }

    @PostMapping("/api/mail/verifyMail")
    public ResponseResult verifyMail(@RequestParam("email")String email,@RequestParam("captcha") String captcha, HttpServletRequest httpServletRequest) {
        String verifyCode =(String) httpServletRequest.getServletContext().getAttribute("verifyCode");
        if (verifyCode.equals(captcha)) {
            return sendMailService.loginByEmail(email);
        } else {
            return ResponseResult.okResult();
        }

    }
}
