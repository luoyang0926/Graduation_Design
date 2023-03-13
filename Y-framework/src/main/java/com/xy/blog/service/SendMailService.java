package com.xy.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.blog.entity.SysUser;
import com.xy.blog.utils.ResponseResult;

public interface SendMailService extends IService<SysUser> {
    ResponseResult loginByEmail(String email);
}
