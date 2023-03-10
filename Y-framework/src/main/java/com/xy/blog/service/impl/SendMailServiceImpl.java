package com.xy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.blog.entity.LoginUser;
import com.xy.blog.entity.SysUser;
import com.xy.blog.mapper.SysUserMapper;
import com.xy.blog.service.SendMailService;
import com.xy.blog.utils.BeanCopyUtils;
import com.xy.blog.utils.JwtUtil;
import com.xy.blog.utils.RedisCache;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.vo.BlogUserLoginVo;
import com.xy.blog.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SendMailServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SendMailService {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysUserServiceImpl sysUserService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public ResponseResult loginByEmail(String email) {
        LambdaQueryWrapper<SysUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getEmail, email);
        queryWrapper.eq(SysUser::getStatus, 0);
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);
        sysUser.setEmail(email);
        System.out.println(".............................."+sysUser);
        sysUserMapper.updateById(sysUser);
        String password = "qwer1234";
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(sysUser.getUserName(), password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //????????????????????????
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("????????????????????????");
        }
        //??????userid,??????token
        LoginUser user=(LoginUser) authenticate.getPrincipal();
        String id = user.getSysUser().getId().toString();
        String jwt = JwtUtil.createJWT(id);
        //?????????????????????redis
        redisCache.setCacheObject("BlogLogin:"+id,user);

        //System.out.println("=================>"+ SecurityUtils.getLoginUser());

        //???token???userInfo???????????? ??????
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user.getSysUser(), UserInfoVo.class);
        BlogUserLoginVo vo = new BlogUserLoginVo(jwt, userInfoVo);
        return ResponseResult.okResult(vo);
    }
}
