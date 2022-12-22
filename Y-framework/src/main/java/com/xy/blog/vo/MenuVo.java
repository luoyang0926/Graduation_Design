package com.xy.blog.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xy.blog.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuVo {

    //菜单ID@TableId
    private Long id;
    //父菜单ID
    private Long parentId;
    //菜单名称

    private String  label;
   // private String  status;

    private List<Menu> children;


}
