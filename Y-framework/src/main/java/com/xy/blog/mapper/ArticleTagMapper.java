package com.xy.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.blog.entity.ArticleTag;

import java.util.List;


/**
 * 文章标签关联表(ArticleTag)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-05 10:27:14
 */
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {

    List<Long> selectTagId(Long id);


    void insertTag(Long articleId, Long tagId);

    int deleteByAid(Long id);
}

