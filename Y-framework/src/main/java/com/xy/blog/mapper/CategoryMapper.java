package com.xy.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.blog.entity.Category;
import org.springframework.stereotype.Repository;


/**
 * 分类表(Category)表数据库访问层
 *
 * @author makejava
 * @since 2022-11-22 15:50:26
 */
@Repository
public interface CategoryMapper extends BaseMapper<Category> {

}

