package com.xy.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.blog.entity.Article;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.vo.AddArticleDto;
import com.xy.blog.vo.ArticleListDto;
import com.xy.blog.vo.PageVo;

public interface ArticleService extends IService<Article> {
    ResponseResult hotArticleList();


    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId);

    ResponseResult getArticleById(Long id);

    ResponseResult updateViewCount(Long articleId);

    ResponseResult AdminArticleList(int pageNum, int pageSize);

    ResponseResult add(AddArticleDto article);

    ResponseResult<PageVo> pageArticleList(int pageNum, int pageSize, ArticleListDto articleListDto);

    ResponseResult getArticle(Long id);

    ResponseResult utDArticleList();
}
