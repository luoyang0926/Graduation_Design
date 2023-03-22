package com.xy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xy.blog.entity.LoginUser;
import com.xy.blog.entity.SysUser;
import com.xy.blog.mapper.ArticleMapper;
import com.xy.blog.mapper.SysUserMapper;
import com.xy.blog.service.BlogLoginService;
import com.xy.blog.utils.*;
import com.xy.blog.vo.BlogUserLoginVo;
import com.xy.blog.vo.LoginUserVo;
import com.xy.blog.vo.UserInfoVo;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BlogLoginServiceImpl implements BlogLoginService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private RedisCache redisCache;
    @Override
    public ResponseResult   login(LoginUserVo userVo) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userVo.getUsername(), userVo.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //判断是否认证通过
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("用户名或密码错误");
        }

        //获取userid,生成token
        LoginUser user=(LoginUser) authenticate.getPrincipal();
        String id = user.getSysUser().getId().toString();
        String jwt = JwtUtil.createJWT(id);
        //把用户信息存入redis
        redisCache.setCacheObject("BlogLogin:"+id,user);

        //System.out.println("=================>"+ SecurityUtils.getLoginUser());

        //把token和userInfo封装起来 返回
        Long articleCount = articleMapper.getArticleCount();
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user.getSysUser(), UserInfoVo.class);
        BlogUserLoginVo vo = new BlogUserLoginVo(jwt, userInfoVo,articleCount);
        return ResponseResult.okResult(vo);
    }

    @Override
    public ResponseResult logout() {
        //获取token 解析获取userid
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        //获取userid
        Long userId = loginUser.getSysUser().getId();
        //删除redis中的用户信息
        redisCache.deleteObject("BlogLogin:"+userId);
        return ResponseResult.okResult();
    }



}
