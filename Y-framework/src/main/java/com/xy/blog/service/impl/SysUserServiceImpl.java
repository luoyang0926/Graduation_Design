package com.xy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.blog.entity.Role;
import com.xy.blog.entity.SysUser;
import com.xy.blog.entity.UserRole;
import com.xy.blog.enums.AppHttpCodeEnum;
import com.xy.blog.execption.SystemException;
import com.xy.blog.mapper.RoleMapper;
import com.xy.blog.mapper.SysUserMapper;
import com.xy.blog.mapper.UserRoleMapper;
import com.xy.blog.service.SysUserService;
import com.xy.blog.utils.BeanCopyUtils;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.utils.SecurityUtils;
import com.xy.blog.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 用户表(SysUser)表服务实现类
 *
 * @author makejava
 * @since 2022-11-23 10:56:57
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {


    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public ResponseResult userInfo(Long userId) {

       // Long userId = SecurityUtils.getUserId();
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getId, userId);
        SysUser sysUser = userMapper.selectOne(queryWrapper);
        UserInfoVo vo=new UserInfoVo();
        BeanUtils.copyProperties(sysUser,vo);
        return ResponseResult.okResult(vo);
    }

    @Override
    public ResponseResult updateUserInfo(SysUser user) {
        this.updateById(user);
        return ResponseResult.okResult();

    }

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public ResponseResult register(SysUser user) {
        System.out.println("??????????????????"+user);
        //对数据进行非空判断
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getPassword())){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        /*if(!StringUtils.hasText(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }*/
        /*if(!StringUtils.hasText(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }*/
        //对数据进行是否存在的判断
        if(userNameExist(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
       /* if(nickNameExist(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_EXIST);
        }*/
        //...
        //对密码进行加密
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        //存入数据库
        save(user);
        return ResponseResult.okResult();
    }

    private boolean nickNameExist(String nickName) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getNickName, nickName);
        return count(queryWrapper)>0;
    }

    private boolean userNameExist(String userName) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUserName, userName);
        return count(queryWrapper)>0;

    }

    @Override
    public ResponseResult<PageVo> pageUserList(int pageNum, int pageSize, SysUser sysUser) {
        LambdaQueryWrapper<SysUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(sysUser.getUserName()), SysUser::getUserName, sysUser.getUserName());
        queryWrapper.eq(StringUtils.hasText(sysUser.getPhonenumber()), SysUser::getPhonenumber, sysUser.getPhonenumber());
        queryWrapper.eq(StringUtils.hasText(sysUser.getStatus()), SysUser::getStatus, sysUser.getStatus());
        queryWrapper.eq(SysUser::getStatus, 0);
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        PageVo pageVo = new PageVo(page.getRecords(),page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getUser(Long id) {
        SysUser user = userMapper.selectById(id);
        List<Long> roleIds = userRoleMapper.selectRoleIds(id);
        LambdaQueryWrapper<Role> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getStatus, 0);
        List<Role> roles = roleMapper.selectList(queryWrapper);
        HXUserDto userDto = new HXUserDto();
        userDto.setUser(user);
        userDto.setRoles(roles);
        userDto.setRoleIds(roleIds);
        return ResponseResult.okResult(userDto);

    }

   /* @Override
    public ResponseResult add(SysUser user) {
        String password = user.getPassword();
        String encode = passwordEncoder.encode(password);
        user.setPassword(encode);
        userMapper.insert(user);
        Long userId = user.getId();
        user.get
        userRoleMapper.insert()
        return  ResponseResult.okResult();
    }*/


    @Override
    public ResponseResult addUser(AddUserDto userDto) {
        String password = userDto.getPassword();
        String encode = passwordEncoder.encode(password);
        Long id = SecurityUtils.getUserId();
        System.out.println("==========================>"+id);
        userDto.setCreateBy(id);
        userDto.setCreateTime(new Date());
        userDto.setUpdateBy(SecurityUtils.getUserId());
        userDto.setUpdateTime(new Date());
        userDto.setPassword(encode);
        userMapper.insertUser(userDto);

        Long userId = userDto.getId();
        List<Long> roleIds = userDto.getRoleIds();
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateUserById(UpdateUserDto userDto) {
        System.out.println("========================>"+userDto.getId());
         userRoleMapper.delete(userDto.getId());
        SysUser sysUser = BeanCopyUtils.copyBean(userDto, SysUser.class);
        userMapper.updateById(sysUser);
        List<Long> roleIds = userDto.getRoleIds();
        for (Long roleId : roleIds) {
            UserRole userRole=new UserRole();
            userRole.setUserId(userDto.getId());
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
        return ResponseResult.okResult();
    }
}

