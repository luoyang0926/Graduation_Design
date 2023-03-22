package com.xy.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.blog.entity.Role;
import com.xy.blog.vo.ChangeStatusVo;
import org.springframework.stereotype.Repository;


/**
 * 角色信息表(Role)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-06 19:32:12
 */
@Repository
public interface RoleMapper extends BaseMapper<Role> {

    void updateStatus(ChangeStatusVo changeStatusVo);
}

