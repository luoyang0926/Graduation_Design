package com.xy.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.blog.entity.Menu;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.vo.AddMenuDto;
import com.xy.blog.vo.MenuDto;

import java.util.List;


/**
 * 菜单权限表(Menu)表服务接口
 *
 * @author makejava
 * @since 2022-12-02 17:57:39
 */
public interface MenuService extends IService<Menu> {

    List<String> selectPermsByUserId(Long id);

    List<Menu> selectRouterMenuTreeByUserId(Long userId);

    ResponseResult listPageMenu( MenuDto menuDto);

    ResponseResult addMenu(Menu menu);

    ResponseResult updateMenu(Menu menu);

    ResponseResult getTreeMenu();

    ResponseResult selectTreeById(Long id);

}
