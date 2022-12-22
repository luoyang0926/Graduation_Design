package com.xy.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.blog.entity.Article;

public interface ArticleMapper extends BaseMapper<Article> {

    void updateViewCount(Article article);
}
