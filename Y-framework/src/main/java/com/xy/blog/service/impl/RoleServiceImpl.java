package com.xy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.blog.entity.Role;
import com.xy.blog.entity.SysRoleMenu;
import com.xy.blog.mapper.RoleMapper;
import com.xy.blog.mapper.SysRoleMenuMapper;
import com.xy.blog.service.RoleService;
import com.xy.blog.utils.BeanCopyUtils;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.vo.AddRoleDto;
import com.xy.blog.vo.ChangeStatusVo;
import com.xy.blog.vo.PageVo;
import lombok.experimental.Accessors;
import lombok.experimental.PackagePrivate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author makejava
 * @since 2022-12-06 19:32:12
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Override
    public ResponseResult getRoleList() {
        LambdaQueryWrapper<Role> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getStatus, 0);
        List<Role> roles = roleMapper.selectList(queryWrapper);
        return ResponseResult.okResult(roles);
    }

    @Override
    public ResponseResult listPageRole(Integer pageNum, Integer pageSize, String roleName,String status) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(status),Role::getStatus, status);
        queryWrapper.like(StringUtils.hasText(roleName), Role::getRoleName, roleName);
        queryWrapper.eq(Role::getStatus, 0);
        Page<Role> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        //封装数据返回
        PageVo pageVo = new PageVo(page.getRecords(),page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult addRole(AddRoleDto addRoleDto) {
        Role role = BeanCopyUtils.copyBean(addRoleDto, Role.class);
        roleMapper.insert(role);
        List<Long> menuIds = addRoleDto.getMenuIds();
        menuIds.stream().map(menuId -> {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(role.getId());
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
            return null;
        }).collect(Collectors.toList());
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateRole(AddRoleDto addRoleDto) {
        LambdaQueryWrapper<SysRoleMenu> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, addRoleDto.getId());
        roleMenuMapper.delete(queryWrapper);
        List<Long> menuIds = addRoleDto.getMenuIds();
        for (Long menuId : menuIds) {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(addRoleDto.getId());
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
        Role role = BeanCopyUtils.copyBean(addRoleDto, Role.class);
        roleMapper.updateById(role);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult changeStatus(ChangeStatusVo changeStatusVo) {
        roleMapper.updateStatus(changeStatusVo);
        return ResponseResult.okResult();
    }
}

