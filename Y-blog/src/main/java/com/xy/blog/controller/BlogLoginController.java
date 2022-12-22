package com.xy.blog.controller;

import com.xy.blog.entity.SysUser;
import com.xy.blog.enums.AppHttpCodeEnum;
import com.xy.blog.execption.SystemException;
import com.xy.blog.service.BlogLoginService;
import com.xy.blog.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlogLoginController {

    @Autowired
    private BlogLoginService loginService;
    @PostMapping("/login")
    public ResponseResult login(@RequestBody SysUser sysUser) {

        if (!StringUtils.hasText(sysUser.getUserName())) {
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return loginService.login(sysUser);
    }

    @PostMapping("/logout")
    public ResponseResult logout(){
        return loginService.logout();
    }


}
