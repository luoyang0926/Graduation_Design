package com.xy.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.blog.entity.Role;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.vo.AddRoleDto;
import com.xy.blog.vo.ChangeStatusVo;

import java.awt.geom.RectangularShape;


/**
 * 角色信息表(Role)表服务接口
 *
 * @author makejava
 * @since 2022-12-06 19:32:12
 */
public interface RoleService extends IService<Role> {

    ResponseResult getRoleList();

    ResponseResult listPageRole(Integer pageNum, Integer pageSize, String roleName,String status);

    ResponseResult addRole(AddRoleDto addRoleDto);

    ResponseResult updateRole(AddRoleDto addRoleDto);

    ResponseResult changeStatus(ChangeStatusVo changeStatusVo);
}
