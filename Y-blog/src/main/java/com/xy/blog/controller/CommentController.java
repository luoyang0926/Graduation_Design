package com.xy.blog.controller;

import com.xy.blog.entity.Comment;
import com.xy.blog.service.CommentService;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.utils.SystemConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@Api(tags = "评论",description = "评论相关借口")
public class CommentController {


    @Autowired
    private CommentService commentService;


    @GetMapping("/commentList")
    public ResponseResult commentList(Integer pageNum, Integer pageSize,  Long articleId) {
        return commentService.getCommentList(SystemConstants.ARTICLE_COMMENT,pageNum, pageSize, articleId);

    }
    @PostMapping
    public ResponseResult addComment(@RequestBody Comment comment){

        return commentService.addComment(comment);
    }

    @GetMapping("/linkCommentList")
    @ApiOperation(value = "友链评论列表",notes = "获取一页友链评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum",value = "页号"),
            @ApiImplicitParam(name = "pageSize",value = "每页大小")
    }
    )
    public ResponseResult linkCommentList(Integer pageNum,Integer pageSize){
        return commentService.getLinkCommentList(SystemConstants.LINK_COMMENT,pageNum,pageSize);
    }

    @DeleteMapping("/deleteMyComment/{id}")
    public ResponseResult deleteMyComment(@PathVariable("id") Long id) {
        commentService.deleteMyComment(id);
        return ResponseResult.okResult();
    }
}
