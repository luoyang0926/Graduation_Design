package com.xy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.blog.entity.Article;
import com.xy.blog.entity.Category;
import com.xy.blog.entity.Tag;
import com.xy.blog.mapper.CategoryMapper;
import com.xy.blog.service.ArticleService;
import com.xy.blog.service.CategoryService;
import com.xy.blog.utils.BeanCopyUtils;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.utils.SecurityUtils;
import com.xy.blog.utils.SystemConstants;
import com.xy.blog.vo.CategoryListDto;
import com.xy.blog.vo.CategoryVo;
import com.xy.blog.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 分类表(Category)表服务实现类
 *
 * @author makejava
 * @since 2022-11-22 15:51:51
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public ResponseResult getCategoryList() {
        /*LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        List<Article> list = articleService.list(queryWrapper);
        Set<Long> categoryIds = list.stream().map(article -> article.getCategoryId()).collect(Collectors.toSet());

        List<Category> categories = this.listByIds(categoryIds);
        categories = categories.stream().
                filter(category -> SystemConstants.STATUS_NORMAL.equals(category.getStatus()))
                .collect(Collectors.toList());
        //封装vo
        List<CategoryVo> categoryVos = BeanCopyUtils.copyBeanList(categories, CategoryVo.class);

        return ResponseResult.okResult(categoryVos);*/
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getStatus, 0);
        queryWrapper.eq(Category::getDelFlag, 0);
        List<Category> categoryList = categoryService.list(queryWrapper);
        List<CategoryVo> categoryVos = BeanCopyUtils.copyBeanList(categoryList, CategoryVo.class);
        return ResponseResult.okResult(categoryVos);

    }

    @Override
    public ResponseResult pageCategoryList(Integer pageNum, Integer pageSize, CategoryListDto categoryListDto) {
        //分页查询
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(categoryListDto.getName()), Category::getName, categoryListDto.getName());
        queryWrapper.eq(StringUtils.hasText(categoryListDto.getDescription()), Category::getDescription, categoryListDto.getDescription());
        queryWrapper.eq(StringUtils.hasText(categoryListDto.getStatus()), Category::getStatus, categoryListDto.getStatus());
        Page<Category> page = new Page<>(pageNum,pageSize);
        page(page, queryWrapper);
        //封装数据返回
        PageVo pageVo = new PageVo(page.getRecords(),page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult selectCategory(Long id) {
        Category category = categoryMapper.selectById(id);
        return ResponseResult.okResult(category);
    }

    @Override
    public ResponseResult updateCategory(Category category) {
        Long userId = SecurityUtils.getUserId();
        category.setUpdateBy(userId);
        //tagMapper.updateTagById(tag);
        LambdaUpdateWrapper<Category> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(Category::getId, category.getId());
        categoryMapper.update(category, updateWrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult add(Category category) {
        categoryMapper.insert(category);
        return ResponseResult.okResult();
    }
}

