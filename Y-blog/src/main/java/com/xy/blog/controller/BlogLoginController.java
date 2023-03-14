package com.xy.blog.controller;

import com.xy.blog.entity.Captcha;
import com.xy.blog.entity.SysUser;
import com.xy.blog.enums.AppHttpCodeEnum;
import com.xy.blog.execption.SystemException;
import com.xy.blog.service.BlogLoginService;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.utils.ValidateCodeUtil;
import com.xy.blog.vo.LoginUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class BlogLoginController {

    @Autowired
    private BlogLoginService loginService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody LoginUserVo userVo,HttpServletRequest httpServletRequest) {

        if (!StringUtils.hasText(userVo.getUsername())) {
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
            //toLowerCase() 不区分大小写进行验证码校验
            String sessionCode= String.valueOf(httpServletRequest.getServletContext().getAttribute("XYCODE")).toLowerCase();
            System.out.println("session里的验证码："+sessionCode);
            String receivedCode=userVo.getCaptcha().toLowerCase();
            System.out.println("用户的验证码："+receivedCode);
        if (!receivedCode.equals(sessionCode)) {
           throw  new SystemException(AppHttpCodeEnum.CODE_FALSE);
        }
        return loginService.login(userVo);
    }

    @PostMapping("/logout")
    public ResponseResult logout() {
        return loginService.logout();
    }

 /*   @GetMapping("/api/user/captchaImage")
    public void getCaptchaImg(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        try {
            response.setContentType("image/png");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Expire", "0");
            response.setHeader("Pragma", "no-cache");
            ValidateCodeUtil validateCode = new ValidateCodeUtil();
            // getRandomCodeImage方法会直接将生成的验证码图片写入response
            validateCode.getRandomCodeImage(request, response);
            // System.out.println("session里面存储的验证码为："+session.getAttribute("JCCODE"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
/*   // 验证码校验
    @GetMapping("/api/user/login/account")
    public boolean getCheckCaptcha(@RequestParam("captcha") String captcha, HttpSession session) {

        try {
            //toLowerCase() 不区分大小写进行验证码校验
            String sessionCode= String.valueOf(session.getAttribute("JCCODE")).toLowerCase();
            System.out.println("session里的验证码："+sessionCode);
            String receivedCode=captcha.toLowerCase();
            System.out.println("用户的验证码："+receivedCode);
            return !sessionCode.equals("") && !receivedCode.equals("") && sessionCode.equals(receivedCode);

        } catch (Exception e) {

            return false;
        }

    }*/
    // 生成验证码,返回的是 base64
    @GetMapping("/api/user/captchaImage")
    public Object getCaptchaBase64(HttpServletRequest request, HttpServletResponse response) {
        Captcha captcha = new Captcha();
        Map result = new HashMap();
        try {

            response.setContentType("image/png");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Expire", "0");
            response.setHeader("Pragma", "no-cache");
            ValidateCodeUtil validateCode = new ValidateCodeUtil();
            // 返回base64
            String base64String = validateCode.getRandomCodeBase64(request, response);
            result.put("url", "data:image/png;base64," + base64String);
           // result.put("message", "created successfull");
            //http://tool.chinaz.com/tools/imgtobase/  base64直接转为图片网站
            System.out.println("结果:" + result.get("url"));
            captcha.setCaptcha(base64String);
            String uuid = UUID.randomUUID().toString().replace("-", "");
            captcha.setUuid(uuid);
        } catch (Exception e) {
            System.out.println(e);
        }

        return ResponseResult.okResult(captcha);
    }
}
