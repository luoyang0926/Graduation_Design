package com.xy.blog.controller;

import com.xy.blog.annotation.SystemLog;
import com.xy.blog.entity.SysUser;
import com.xy.blog.service.SysUserService;
import com.xy.blog.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private SysUserService userService;

    @GetMapping("/userInfo")
    public ResponseResult userInfo(@RequestParam("userId") Long userId){
        return userService.userInfo(userId);
    }

    @PutMapping("/userInfo")
    @SystemLog(businessName ="更新用户信息")
    public ResponseResult updateUserInfo(@RequestBody SysUser user){
        return userService.updateUserInfo(user);
    }
    @PostMapping("/register")
    public ResponseResult register(@RequestBody SysUser user){
        return userService.register(user);
    }
}
