package com.xy.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.blog.entity.UserRole;
import com.xy.blog.utils.ResponseResult;

import java.util.List;


/**
 * 用户和角色关联表(UserRole)表服务接口
 *
 * @author makejava
 * @since 2022-12-02 18:03:49
 */
public interface UserRoleService extends IService<UserRole> {

    List<String> selectRoleKeyByUserId(Long id);

}
