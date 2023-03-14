package com.xy.blog.vo;

import com.xy.blog.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserVo {
    private String   username;
    private String   password;
    private String   captcha;
    private String   uuid;
}
