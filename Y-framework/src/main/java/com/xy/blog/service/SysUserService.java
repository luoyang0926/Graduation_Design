package com.xy.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.blog.entity.SysUser;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.vo.AddUserDto;
import com.xy.blog.vo.HXUserDto;
import com.xy.blog.vo.UpdateUserDto;


/**
 * 用户表(SysUser)表服务接口
 *
 * @author makejava
 * @since 2022-11-23 10:56:57
 */
public interface SysUserService extends IService<SysUser> {

    ResponseResult userInfo();

    ResponseResult updateUserInfo(SysUser user);

    ResponseResult register(SysUser user);

    ResponseResult pageUserList(int pageNum, int pageSize, SysUser sysUser);


    ResponseResult getUser(Long id);


    ResponseResult addUser(AddUserDto userDto);

    ResponseResult updateUserById(UpdateUserDto userDto);
}
