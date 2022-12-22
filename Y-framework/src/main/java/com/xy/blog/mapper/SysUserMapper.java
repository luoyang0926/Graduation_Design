package com.xy.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.blog.entity.SysUser;
import com.xy.blog.vo.AddUserDto;


/**
 * 用户表(SysUser)表数据库访问层
 *
 * @author makejava
 * @since 2022-11-23 10:57:06
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    void insertUser(AddUserDto userDto);

}

