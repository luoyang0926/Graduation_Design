package com.xy.blog.controller;

import com.xy.blog.entity.SysUser;
import com.xy.blog.service.RoleService;
import com.xy.blog.service.SysUserService;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.vo.AddUserDto;
import com.xy.blog.vo.PageVo;
import com.xy.blog.vo.HXUserDto;
import com.xy.blog.vo.UpdateUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(("/system"))
public class UserController {

    @Autowired
    private SysUserService userService;
    @Autowired
    private RoleService roleService;


    @PostMapping("/user")
    public ResponseResult addUser(@RequestBody AddUserDto userDto) {
        userService.addUser(userDto);
        return ResponseResult.okResult();
    }

    @GetMapping("/role/listAllRole")
    public ResponseResult getRoleList() {
        return   roleService.getRoleList();

    }

    @GetMapping("/user/list")
    public ResponseResult<PageVo> pageUserList(int pageNum, int pageSize, SysUser sysUser) {
        return    userService.pageUserList(pageNum,pageSize,sysUser);

    }

    @GetMapping("/user/{id}")
    public ResponseResult HXUser(@PathVariable("id") Long id) {
        return userService.getUser(id);
    }

    @DeleteMapping("/user/{ids}")
    public ResponseResult deleteUser(@PathVariable("ids") List<Long> ids) {
        userService.removeByIds(ids);
        return ResponseResult.okResult();
    }

    @PutMapping("/user")
    public ResponseResult updateUser(@RequestBody UpdateUserDto userDto) {
        System.out.println("======================>"+userDto);
        userService.updateUserById(userDto);
        return ResponseResult.okResult();
    }
}
