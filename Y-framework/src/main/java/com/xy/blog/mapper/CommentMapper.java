package com.xy.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.blog.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 评论表(Comment)表数据库访问层
 *
 * @author makejava
 * @since 2022-11-23 09:42:20
 */

@Repository
public interface CommentMapper extends BaseMapper<Comment> {
    List<Comment> commentByArticleId(@Param("article_id") Long articleId,@Param("pageNum") Integer pageNum,@Param("pageSize") Integer pageSize );

    Long selectCommentCount(Long articleId);
}

