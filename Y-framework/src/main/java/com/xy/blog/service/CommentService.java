package com.xy.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.blog.entity.Comment;
import com.xy.blog.utils.ResponseResult;


/**
 * 评论表(Comment)表服务接口
 *
 * @author makejava
 * @since 2022-11-23 09:42:44
 */
public interface CommentService extends IService<Comment> {

    ResponseResult getCommentList(String commentType, Integer pageNum, Integer pageSize, Long articleId);

    ResponseResult addComment(Comment comment);


    ResponseResult deleteMyComment(Long id);
}

