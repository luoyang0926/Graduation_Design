package com.xy.blog.controller;

import com.xy.blog.entity.LoginUser;
import com.xy.blog.entity.Menu;
import com.xy.blog.entity.SysUser;
import com.xy.blog.entity.UserRole;
import com.xy.blog.enums.AppHttpCodeEnum;
import com.xy.blog.execption.SystemException;
import com.xy.blog.service.LoginService;
import com.xy.blog.service.MenuService;
import com.xy.blog.service.UserRoleService;
import com.xy.blog.utils.BeanCopyUtils;
import com.xy.blog.utils.RedisCache;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.utils.SecurityUtils;
import com.xy.blog.vo.AdminUserInfoVo;
import com.xy.blog.vo.RoutersVo;
import com.xy.blog.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RedisCache redisCache;


    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody SysUser user) {
        if (!StringUtils.hasText(user.getUserName())) {
            //提示 必须要传用户名
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return loginService.login(user);
    }

    @GetMapping("/getInfo")
    public ResponseResult<AdminUserInfoVo> getInfo() {
        //获取当前登录的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //根据用户id查询权限信息
        List<String> perms = menuService.selectPermsByUserId(loginUser.getSysUser().getId());
        //根据用户id查询角色信息
        List<String> roleKeyList = userRoleService.selectRoleKeyByUserId(loginUser.getSysUser().getId());
        //获取用户信息
        SysUser sysUser = loginUser.getSysUser();
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(sysUser, UserInfoVo.class);
        //封装数据返回

        AdminUserInfoVo adminUserInfoVo = new AdminUserInfoVo(perms, roleKeyList, userInfoVo);
        return ResponseResult.okResult(adminUserInfoVo);

    }

    @GetMapping("getRouters")
    public ResponseResult<RoutersVo> getRouters() {
        Long userId = SecurityUtils.getUserId();
        //查询menu 结果是tree的形式
        List<Menu> menus = menuService.selectRouterMenuTreeByUserId(userId);
        //封装数据返回
        return ResponseResult.okResult(new RoutersVo(menus));
    }

    @PostMapping("/user/logout")
    public ResponseResult logout() {
        return loginService.logout();
    }
}
