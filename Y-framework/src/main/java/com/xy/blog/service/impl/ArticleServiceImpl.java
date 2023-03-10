package com.xy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.blog.entity.Article;
import com.xy.blog.entity.ArticleTag;
import com.xy.blog.entity.Category;
import com.xy.blog.mapper.ArticleMapper;
import com.xy.blog.service.ArticleService;
import com.xy.blog.service.ArticleTagService;
import com.xy.blog.service.CategoryService;
import com.xy.blog.utils.BeanCopyUtils;
import com.xy.blog.utils.RedisCache;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.utils.SystemConstants;
import com.xy.blog.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {


    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ArticleTagService articleTagService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public ResponseResult hotArticleList() {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        queryWrapper.orderByDesc(Article::getViewCount);
        Page<Article> page = new Page(1, 10);
        page(page, queryWrapper);
        List<Article> articles= page.getRecords();
        List<HotArticleVo> articleVos = new ArrayList<>();
        for (Article article : articles) {
            HotArticleVo vo = new HotArticleVo();
            BeanUtils.copyProperties(article,vo);
            articleVos.add(vo);
        }
        return ResponseResult.okResult(articleVos);
    }
    @Override
    public ResponseResult utDArticleList() {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        queryWrapper.orderByDesc(Article::getCreateTime);
        Page<Article> page = new Page<>(1, 5);
        page(page, queryWrapper);
        List<Article> records = page.getRecords();
        List<UtdArticleVo> articleVos = new ArrayList<>();
        for (Article article : records) {
            UtdArticleVo vo = new UtdArticleVo();
            BeanUtils.copyProperties(article,vo);
            articleVos.add(vo);
        }
        return ResponseResult.okResult(articleVos);
    }


    @Override
    public ResponseResult   articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        //????????????
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // ?????? ???categoryId ?????? ??????????????????????????????
        lambdaQueryWrapper.eq(Objects.nonNull(categoryId)&&categoryId>0 ,Article::getCategoryId,categoryId);
        // ????????????????????????
        lambdaQueryWrapper.eq(Article::getStatus,SystemConstants.ARTICLE_STATUS_NORMAL);
        // ???isTop????????????
        lambdaQueryWrapper.orderByDesc(Article::getIsTop);

        //????????????
        Page<Article> page = new Page<>(pageNum,pageSize);
        page(page,lambdaQueryWrapper);

        List<Article> articles = page.getRecords();
        //??????categoryName
        articles.stream()
                .map(article -> article.setCategoryName(categoryService.getById(article.getCategoryId()).getName()))
                .collect(Collectors.toList());
        //articleId?????????articleName????????????
//        for (Article article : articles) {
//            Category category = categoryService.getById(article.getCategoryId());
//            article.setCategoryName(category.getName());
//        }

        //??????????????????
        List<ArticleListVo> articleListVos = BeanCopyUtils.copyBeanList(page.getRecords(), ArticleListVo.class);

        PageVo pageVo = new PageVo(articleListVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticleById(Long id) {
        Article article = this.getById(id);
        //???redis?????????viewCount
       Integer viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
        article.setViewCount(viewCount.longValue());
        //?????????VO
        ArticleDetailVo articleDetailVo = BeanCopyUtils.copyBean(article, ArticleDetailVo.class);
        //????????????id???????????????
        Long categoryId = articleDetailVo.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if(category!=null){
            articleDetailVo.setCategoryName(category.getName());
        }
        //??????????????????
        return ResponseResult.okResult(articleDetailVo);
    }

    @Override
    public ResponseResult updateViewCount(Long articleId) {

        //update sg_article set view_count=view_count+1 where id=#{articleId};
       /*   LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId, articleId);
        updateWrapper.setSql("view_count=view_count+1");
        this.update(updateWrapper);*/
        //??????redis????????? id????????????
        redisCache.incrementCacheMapValue("article:viewCount",articleId.toString(),1);
        return ResponseResult.okResult();

    }

    @Override
    public ResponseResult AdminArticleList(int pageNum, int pageSize) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, 0);
        Page<Article> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        //??????????????????
        List<ArticleDetailVo> articleDetailVos = BeanCopyUtils.copyBeanList(page.getRecords(), ArticleDetailVo.class);
        PageVo pageVo = new PageVo(articleDetailVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    @Transactional
    public ResponseResult add(AddArticleDto articleDto) {
        //?????? ??????
        Article article = BeanCopyUtils.copyBean(articleDto, Article.class);
        this.save(article);


        List<ArticleTag> articleTags = articleDto.getTags().stream()
                .map(tagId -> new ArticleTag(article.getId(), tagId))
                .collect(Collectors.toList());

        //?????? ????????????????????????
        articleTagService.saveBatch(articleTags);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult pageArticleList(int pageNum, int pageSize, ArticleListDto articleListDto) {
        //????????????
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(articleListDto.getTitle()), Article::getTitle, articleListDto.getTitle());
        queryWrapper.like(StringUtils.hasText(articleListDto.getSummary()), Article::getSummary, articleListDto.getSummary());
        queryWrapper.eq(Article::getStatus, 0);
        Page<Article> page = new Page<>(pageNum,pageSize);
        page(page, queryWrapper);
        //??????????????????
        PageVo pageVo = new PageVo(page.getRecords(),page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticle(Long id) {
        Article article = articleMapper.selectById(id);
        AddArticleDto addArticleDto = BeanCopyUtils.copyBean(article, AddArticleDto.class);
        return ResponseResult.okResult(addArticleDto);
    }



}

