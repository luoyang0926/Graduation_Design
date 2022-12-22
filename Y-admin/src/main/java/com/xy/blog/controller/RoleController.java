package com.xy.blog.controller;

import com.xy.blog.entity.Role;
import com.xy.blog.service.RoleService;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.vo.AddRoleDto;
import com.xy.blog.vo.ChangeStatusVo;
import com.xy.blog.vo.TagListDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @RequestMapping("/list")
    public ResponseResult listPageRole(Integer pageNum, Integer pageSize,  String roleName,String status) {
      return   roleService.listPageRole(pageNum,pageSize,roleName, status);
    }

    @PostMapping
    public ResponseResult addRole(@RequestBody AddRoleDto addRoleDto) {
        roleService.addRole(addRoleDto);
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult HXRole(@PathVariable("id") Long id) {
        Role role = roleService.getById(id);
        return ResponseResult.okResult(role);
    }

    @PutMapping
    public ResponseResult updateRole(@RequestBody AddRoleDto addRoleDto) {
        roleService.updateRole(addRoleDto);
        return ResponseResult.okResult();
    }

    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(@RequestBody ChangeStatusVo changeStatusVo) {
        roleService.changeStatus(changeStatusVo);
        return ResponseResult.okResult();
    }
}
