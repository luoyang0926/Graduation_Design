package com.xy.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.blog.entity.SysRoleMenu;
import org.springframework.stereotype.Repository;


/**
 * 角色和菜单关联表(SysRoleMenu)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-13 16:37:07
 */
@Repository
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

}

