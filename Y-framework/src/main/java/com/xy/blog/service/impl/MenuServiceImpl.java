package com.xy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.blog.entity.Menu;
import com.xy.blog.entity.SysRoleMenu;
import com.xy.blog.mapper.MenuMapper;
import com.xy.blog.mapper.SysRoleMenuMapper;
import com.xy.blog.mapper.UserRoleMapper;
import com.xy.blog.service.MenuService;
import com.xy.blog.service.UserRoleService;
import com.xy.blog.utils.BeanCopyUtils;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.utils.SecurityUtils;
import com.xy.blog.utils.SystemConstants;
import com.xy.blog.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author makejava
 * @since 2022-12-02 17:57:39
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    private MenuMapper  menuMapper;
    @Autowired
    private SysRoleMenuServiceImpl roleMenuService;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<String> selectPermsByUserId(Long id) {
      Long roleId=userRoleMapper.getRoleId1(id);
        if (roleId == 1L) {
            LambdaQueryWrapper<Menu> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.in(Menu::getMenuType, SystemConstants.MENU, SystemConstants.BUTTON);
            queryWrapper.eq(Menu::getStatus, SystemConstants.STATUS_NORMAL);
            List<Menu> menuList = this.list(queryWrapper);

            List<String> perms = menuList.stream()
                    .map(Menu::getPerms)
                    .collect(Collectors.toList());
            return perms;
        }
        return getBaseMapper().selectPermsByUserId(id);
    }

    @Override
    public List<Menu> selectRouterMenuTreeByUserId(Long userId) {

        //判断是否是管理员
        //   MenuMapper menuMapper = this.getBaseMapper();
        //如果是 获取所有符合要求的Menu
        List<Menu> menus=null;
            Long id = userRoleMapper.getRoleId1(userId);
        if (id.equals(1L)) {
             menus = this.getBaseMapper().selectAllRouterMenu();
        } else {
            //否则  获取当前用户所具有的Menu
            menus = this.getBaseMapper().selectRouterMenuTreeByUserId(userId);
        }
        //构建tree
        //先找出第一层的菜单  然后去找他们的子菜单设置到children属性中
          List<Menu> menuTree=builderMenuTree(menus,0L);
        return menuTree;
    }

    private List<Menu> builderMenuTree(List<Menu> menus, Long parentId) {
       List<Menu> menuTree= menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> menu.setChildren(getChildren(menu,menus)))
                .collect(Collectors.toList());

        return menuTree;
    }

    /**
     * 获取存入参数的 子Menu集合
     * @param menu
     * @param menus
     * @return
     */
    private List<Menu> getChildren(Menu menu, List<Menu> menus) {
     List<Menu> childrenList=   menus.stream()
                .filter(m -> m.getParentId().equals(menu.getId()))
                .collect(Collectors.toList());

          return childrenList;

    }

    @Override
    public ResponseResult listPageMenu( MenuDto menuDto) {

        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(menuDto.getMenuName()), Menu::getMenuName, menuDto.getMenuName());
        queryWrapper.like(StringUtils.hasText(menuDto.getStatus()), Menu::getStatus, menuDto.getStatus());
        queryWrapper.eq(Menu::getStatus, 0);
      /*  Page<Menu> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        PageVo pageVo = new PageVo(page.getRecords(), page.getTotal());*/
        List<Menu> menus = menuMapper.selectList(queryWrapper);
        return ResponseResult.okResult(menus);
    }

    @Override
    public ResponseResult addMenu(Menu menu) {
        menuMapper.insert(menu);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateMenu(Menu menu) {
        menuMapper.updateById(menu);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getTreeMenu() {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getParentId, 0);
        queryWrapper.eq(Menu::getStatus, 0);
        //一级子菜单
        List<Menu> menuList = menuMapper.selectList(queryWrapper);
        List<Menu> menus=null;
        for (Menu menu : menuList) {
            LambdaQueryWrapper<Menu> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Menu::getParentId, menu.getId());
            //二级子菜单
            //menus = menuMapper.selectList(queryWrapper1);
            menus = menuMapper.selectChildrenMenus(menu.getId());
            menu.setChildren(menus);
            System.out.println("////////////////////->"+menu);
            //三级菜单
            List<Menu> children = menu.getChildren();
            for (Menu menu1 : children) {
                menus = menuMapper.selectChildrenMenus(menu1.getId());
                menu1.setChildren(menus);
            }
        }

        List<MenuVo> menuVos = BeanCopyUtils.copyBeanList(menuList, MenuVo.class);
        System.out.println("------------------------>"+menuVos);
        return ResponseResult.okResult(menuVos);
    }
     @Autowired
     private SysRoleMenuMapper roleMenuMapper;
    @Override
    public ResponseResult selectTreeById(Long id) {
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, id);
        List<SysRoleMenu> roleMenuList = roleMenuMapper.selectList(queryWrapper);
        List<Long> menuIds = roleMenuList.stream().map(sysRoleMenu -> sysRoleMenu.getMenuId()).collect(Collectors.toList());

        LambdaQueryWrapper<Menu> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Menu::getParentId, 0);
        queryWrapper1.eq(Menu::getStatus, 0);
        //一级子菜单
        List<Menu> menuList = menuMapper.selectList(queryWrapper1);
        List<Menu> menus=null;
        for (Menu menu : menuList) {
            LambdaQueryWrapper<Menu> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(Menu::getParentId, menu.getId());
            //二级子菜单
            menus = menuMapper.selectChildrenMenus(menu.getId());
            menu.setChildren(menus);
            //三级菜单
            List<Menu> children = menu.getChildren();
            for (Menu menu1 : children) {
                menus = menuMapper.selectChildrenMenus(menu1.getId());
                menu1.setChildren(menus);
            }
        }
        List<MenuVo> menuVos = BeanCopyUtils.copyBeanList(menuList, MenuVo.class);
        RoleMenuTreeVo roleMenuTreeVo=new RoleMenuTreeVo();
        roleMenuTreeVo.setMenus(menuVos);
        roleMenuTreeVo.setCheckedKeys(menuIds);
        return ResponseResult.okResult(roleMenuTreeVo);

    }
}


