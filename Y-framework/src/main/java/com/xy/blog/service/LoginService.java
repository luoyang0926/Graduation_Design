package com.xy.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.blog.entity.SysUser;
import com.xy.blog.utils.ResponseResult;

public interface LoginService extends IService<SysUser> {
    ResponseResult login(SysUser user);

    ResponseResult logout();
}
