package com.xy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.blog.entity.Comment;
import com.xy.blog.entity.SysUser;
import com.xy.blog.enums.AppHttpCodeEnum;
import com.xy.blog.execption.SystemException;
import com.xy.blog.mapper.CommentMapper;
import com.xy.blog.service.CommentService;
import com.xy.blog.service.SysUserService;
import com.xy.blog.utils.BeanCopyUtils;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.utils.SystemConstants;
import com.xy.blog.vo.CommentVo;
import com.xy.blog.vo.PageVo;
import io.swagger.models.auth.In;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author makejava
 * @since 2022-11-23 09:42:44
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SysUserService userService;
    @Autowired
    private CommentServiceImpl commentService;

    @Override
    public ResponseResult getCommentList(String commentType, Integer pageNum, Integer pageSize, Long articleId) {
/*
        List<Comment> commentList = commentMapper.commentByArticleId(articleId,pageNum,pageSize);
        List<CommentVo> commentVoList = BeanCopyUtils.copyBeanList(commentList, CommentVo.class);*/
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(SystemConstants.ARTICLE_COMMENT.equals(commentType),Comment::getArticleId, articleId);

        queryWrapper.eq(Comment::getRootId, -1);

        //评论类型
        queryWrapper.eq(Comment::getType,commentType);
        queryWrapper.orderByDesc(Comment::getCreateTime);
        Page<Comment> page = new Page(pageNum, pageSize);
        page(page, queryWrapper);


        List<Comment> commentList = page.getRecords();
        List<CommentVo> commentVos = toCommentVoList(commentList);


        Long count= Long.valueOf(commentMapper.selectCommentCount(articleId));

        //查询所有根评论对应的子评论集合，并且赋值给对应的属性
        for (CommentVo commentVo : commentVos) {
            //查询对应的子评论
            List<CommentVo> children = getChildren(commentVo.getId());

            //给rootId=-1的avatarUrl赋值
            SysUser user = userService.getById(commentVo.getCreateBy());
            commentVo.setAvatarUrl(user.getAvatar());

            //给children中的头像赋值
            for (CommentVo commentVo1 : children) {
                Long userId = commentVo1.getCreateBy();
                SysUser sysUser = userService.getById(userId);
                commentVo1.setAvatarUrl(sysUser.getAvatar());
            }
            //赋值
            commentVo.setChildren(children);
            System.out.println("++++++++++++++++++"+commentVos);
        }
        return ResponseResult.okResult(new PageVo(commentVos, count));


    }


    /**
     * 根据根评论的id查询所对应的子评论的集合
     * @param id 根评论的id
     * @return
     */
    private List<CommentVo> getChildren(Long id) {

        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId,id);
        queryWrapper.orderByDesc(Comment::getCreateTime);
        List<Comment> comments = list(queryWrapper);

        List<CommentVo> commentVos = toCommentVoList(comments);
        return commentVos;
    }

    private List<CommentVo> toCommentVoList(List<Comment> commentList) {
        List<CommentVo> commentVoList = BeanCopyUtils.copyBeanList(commentList, CommentVo.class);
        for (CommentVo commentVo : commentVoList) {
            String nickName = userService.getById(commentVo.getCreateBy()).getNickName();
            commentVo.setUsername(nickName);
            //通过toCommentUserId查询用户的昵称并赋值
            //如果toCommentUserId不为-1才进行查询
            if(commentVo.getToCommentUserId()!=-1){
                String toCommentUserName = userService.getById(commentVo.getToCommentUserId()).getNickName();
                commentVo.setToCommentUserName(toCommentUserName);
            }

        }
        return commentVoList;
    }

    @Override
    @Transactional
    public ResponseResult addComment(Comment comment) {
        //评论不能为空
        if (!StringUtils.hasText(comment.getContent())) {
            throw new SystemException(AppHttpCodeEnum.CONTENT_NOT_NULL);
        }
        if(comment.getType().equals("1")){
            comment.setArticleId(-1L);
            this.save(comment);
        }

        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getContent, comment.getContent());
        Comment comment1 = commentMapper.selectOne(queryWrapper);
        String createBy= Long.toString(comment1.getCreateBy());
        if (createBy.equals("-1")) {
            commentMapper.deleteError();
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }


        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteMyComment(Long id) {
        commentMapper.deleteById(id);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getToCommentId,id);
        commentMapper.delete(queryWrapper);
        return ResponseResult.okResult();
    }
    @Override
    public ResponseResult getLinkCommentList(String commentType, Integer pageNum, Integer pageSize) {

        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Comment::getRootId, -1);

        //评论类型
        queryWrapper.eq(Comment::getType,commentType);
        queryWrapper.orderByDesc(Comment::getCreateTime);
        Page<Comment> page = new Page(pageNum, pageSize);
        page(page, queryWrapper);


        List<Comment> commentList = page.getRecords();
        List<CommentVo> commentVos = toCommentVoList(commentList);

       Long count= Long.valueOf(commentMapper.selectLinkCommentCount());

        //查询所有根评论对应的子评论集合，并且赋值给对应的属性
        for (CommentVo commentVo : commentVos) {
            //查询对应的子评论
            List<CommentVo> children = getChildren(commentVo.getId());

            //给rootId=-1的avatarUrl赋值
            SysUser user = userService.getById(commentVo.getCreateBy());
            commentVo.setAvatarUrl(user.getAvatar());

            //给children中的头像赋值
            for (CommentVo commentVo1 : children) {
                Long userId = commentVo1.getCreateBy();
                SysUser sysUser = userService.getById(userId);
                commentVo1.setAvatarUrl(sysUser.getAvatar());
            }
            //赋值
            commentVo.setChildren(children);
            System.out.println("++++++++++++++++++"+commentVos);
        }
        return ResponseResult.okResult(new PageVo(commentVos, count));


    }



}

