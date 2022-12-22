package com.xy.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.blog.entity.Category;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.vo.CategoryListDto;
import com.xy.blog.vo.CategoryVo;
import com.xy.blog.vo.PageVo;
import com.xy.blog.vo.TagListDto;

import java.util.List;


/**
 * 分类表(Category)表服务接口
 *
 * @author makejava
 * @since 2022-11-22 15:51:15
 */
public interface CategoryService extends IService<Category> {


    ResponseResult pageCategoryList(Integer pageNum, Integer pageSize, CategoryListDto categoryListDto);


    ResponseResult getCategoryList();

    ResponseResult selectCategory(Long id);

    ResponseResult updateCategory(Category category);

    ResponseResult add(Category category);
}
