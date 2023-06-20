package com.xy.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.blog.entity.Article;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleMapper extends BaseMapper<Article> {

    void updateViewCount(Article article);

    Integer getArticleCount();

    Integer getMyArticleTotal(String userId);

    Integer getTotalView(String userId);
}
