package com.xy.blog.service;

import com.xy.blog.entity.SysUser;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.vo.LoginUserVo;

public interface BlogLoginService {
    ResponseResult  login(LoginUserVo userVo);

    ResponseResult logout();


}
