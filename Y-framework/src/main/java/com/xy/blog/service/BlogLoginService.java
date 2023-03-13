package com.xy.blog.service;

import com.xy.blog.entity.SysUser;
import com.xy.blog.utils.ResponseResult;

public interface BlogLoginService {
    ResponseResult  login(SysUser sysUser);

    ResponseResult logout();


}
