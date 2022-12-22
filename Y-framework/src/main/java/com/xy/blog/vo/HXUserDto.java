package com.xy.blog.vo;

import com.xy.blog.entity.Role;
import com.xy.blog.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HXUserDto {
    private SysUser user;
    private List<Long> roleIds;
    private List<Role> roles;
}
