package com.xy.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.blog.entity.UserRole;

import java.util.List;


/**
 * 用户和角色关联表(UserRole)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-02 18:03:49
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

    List<String> selectRoleKeyByUserId(Long id);

    List<Long> selectRoleIds(Long id);

    void delete(Long id);

    Long getRoleId1(Long userId);

}

