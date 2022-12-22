package com.xy.blog.controller;

import com.xy.blog.entity.Menu;
import com.xy.blog.service.MenuService;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.vo.AddMenuDto;
import com.xy.blog.vo.MenuDto;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @RequestMapping("/list")
    public ResponseResult list( MenuDto menuDto) {
        return menuService.listPageMenu(menuDto);
    }

    @PostMapping()
    public ResponseResult add(@RequestBody Menu menu) {
        menuService.addMenu(menu);
        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable("id") Long id) {
        menuService.removeById(id);
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult<Menu> HX(@PathVariable("id") Long id) {
        Menu menu = menuService.getById(id);
        return ResponseResult.okResult(menu);
    }

    @PutMapping()
    public ResponseResult update(@RequestBody Menu menu) {
        menuService.updateMenu(menu);
        return ResponseResult.okResult();
    }

    @GetMapping("/treeselect")
    public ResponseResult treeSelect() {
        return   menuService.getTreeMenu();

    }

    @GetMapping("/roleMenuTreeselect/{id}")
    public ResponseResult HXTree(@PathVariable("id") Long id) {
       return    menuService.selectTreeById(id);

    }


}
