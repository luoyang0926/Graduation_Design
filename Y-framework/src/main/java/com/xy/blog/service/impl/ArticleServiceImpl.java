package com.xy.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.blog.entity.Article;
import com.xy.blog.entity.ArticleTag;
import com.xy.blog.entity.Category;
import com.xy.blog.entity.Tag;
import com.xy.blog.enums.AppHttpCodeEnum;
import com.xy.blog.mapper.ArticleMapper;
import com.xy.blog.mapper.ArticleTagMapper;
import com.xy.blog.service.ArticleService;
import com.xy.blog.service.ArticleTagService;
import com.xy.blog.service.CategoryService;
import com.xy.blog.utils.*;
import com.xy.blog.vo.*;
import io.jsonwebtoken.Claims;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
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
    public ResponseResult getMyArticleTotal(HttpServletRequest request) {
        //获取请求头中的token
        String token = request.getHeader("token");
        //解析获取userid
        Claims claims=null;
        try {
            claims = JwtUtil.parseJWT(token);

        } catch (Exception e) {
            //token超时  token非法
            //响应告诉前端需要重新登录
            /*ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
            WebUtils.renderString(response, JSON.toJSONString(result));*/
           /* return;*/
        }
        String userId = claims.getSubject();
        Integer myArticleTotal = articleMapper.getMyArticleTotal(userId);
        MyArticleTotal myArticleTotal1 = new MyArticleTotal();
        myArticleTotal1.setMyArticleTotal(myArticleTotal);
        return ResponseResult.okResult(myArticleTotal1);
    }

    @Override
    public ResponseResult getTotalView(HttpServletRequest request) {
        //获取请求头中的token
        String token = request.getHeader("token");
        //解析获取userid
        Claims claims=null;
        try {
            claims = JwtUtil.parseJWT(token);

        } catch (Exception e) {

        }
        String userId = claims.getSubject();
        Integer totalView = articleMapper.getTotalView(userId);
        GetTotalView totalView1 = new GetTotalView();
        totalView1.setTotalView(totalView);
        return ResponseResult.okResult(totalView1);
    }

    @Override
    public ResponseResult getMyArticleList(Integer pageNum, Integer pageSize, Long categoryId,Long uid) {
        //查询条件
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 如果 有categoryId 就要 查询时要和传入的相同
        lambdaQueryWrapper.eq(Objects.nonNull(categoryId)&&categoryId>0 ,Article::getCategoryId,categoryId);
        // 状态是正式发布的
        lambdaQueryWrapper.eq(Article::getStatus,SystemConstants.ARTICLE_STATUS_NORMAL);
        lambdaQueryWrapper.eq(Article::getCreateBy, uid);
        // 对isTop进行降序
        lambdaQueryWrapper.orderByDesc(Article::getIsTop);

        //分页查询
        Page<Article> page = new Page<>(pageNum,pageSize);
        page(page,lambdaQueryWrapper);

        List<Article> articles = page.getRecords();
        //查询categoryName
        articles.stream()
                .map(article -> article.setCategoryName(categoryService.getById(article.getCategoryId()).getName()))
                .collect(Collectors.toList());
        //封装查询结果
        List<ArticleListVo> articleListVos = BeanCopyUtils.copyBeanList(page.getRecords(), ArticleListVo.class);

        PageVo pageVo = new PageVo(articleListVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticleBySearch(String search) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCount);
        queryWrapper.select(Article::getId, Article::getTitle);
        queryWrapper.like(Article::getTitle, search);
        List<Article> articleList = articleMapper.selectList(queryWrapper);
        return ResponseResult.okResult(BeanCopyUtils.copyBeanList(articleList, ArticleParam.class));
    }

    @Override
    public ResponseResult updateArticle(AddArticleDto articleDto) {

        Article article = BeanCopyUtils.copyBean(articleDto, Article.class);
        articleMapper.updateById(article);
        int i = articleTagMapper.deleteByAid(articleDto.getId());
        System.out.println("??????????????????????"+i);
        List<Long> tags = articleDto.getTags();
        ArticleTag articleTag = new ArticleTag();
        for (Long tagId : tags){
            articleTag.setArticleId(articleDto.getId());
            articleTag.setTagId(tagId);
          /*  Long articleId = article.getId();
            System.out.println("？？？？？？？？？？？"+articleId);
            articleTagMapper.insertTag(articleId,tagId);*/
            articleTagService.save(articleTag);
        }
        return ResponseResult.okResult("发布成功");
    }


    @Override
    public ResponseResult   articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        //查询条件
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 如果 有categoryId 就要 查询时要和传入的相同
        lambdaQueryWrapper.eq(Objects.nonNull(categoryId)&&categoryId>0 ,Article::getCategoryId,categoryId);
        // 状态是正式发布的
        lambdaQueryWrapper.eq(Article::getStatus,SystemConstants.ARTICLE_STATUS_NORMAL);
        // 对isTop进行降序
        lambdaQueryWrapper.orderByDesc(Article::getIsTop);

        //分页查询
        Page<Article> page = new Page<>(pageNum,pageSize);
        page(page,lambdaQueryWrapper);

        List<Article> articles = page.getRecords();
        //查询categoryName
        articles.stream()
                .map(article -> article.setCategoryName(categoryService.getById(article.getCategoryId()).getName()))
                .collect(Collectors.toList());
        //articleId去查询articleName进行设置
//        for (Article article : articles) {
//            Category category = categoryService.getById(article.getCategoryId());
//            article.setCategoryName(category.getName());
//        }

        //封装查询结果
        List<ArticleListVo> articleListVos = BeanCopyUtils.copyBeanList(page.getRecords(), ArticleListVo.class);

        PageVo pageVo = new PageVo(articleListVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticleById(Long id) {
        Article article = this.getById(id);
        //从redis中获取viewCount
       Integer viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
        article.setViewCount(viewCount.longValue());
        //转换成VO
        ArticleDetailVo articleDetailVo = BeanCopyUtils.copyBean(article, ArticleDetailVo.class);
        //根据分类id查询分类名
        Long categoryId = articleDetailVo.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if(category!=null){
            articleDetailVo.setCategoryName(category.getName());
        }
        //封装响应返回
        return ResponseResult.okResult(articleDetailVo);
    }

    @Override
    public ResponseResult updateViewCount(Long articleId) {

        //update sg_article set view_count=view_count+1 where id=#{articleId};
       /*   LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId, articleId);
        updateWrapper.setSql("view_count=view_count+1");
        this.update(updateWrapper);*/
        //更新redis中对应 id的浏览量
        redisCache.incrementCacheMapValue("article:viewCount",articleId.toString(),1);
        return ResponseResult.okResult();

    }

    @Override
    public ResponseResult AdminArticleList(int pageNum, int pageSize) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, 0);
        Page<Article> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        //封装查询结果
        List<ArticleDetailVo> articleDetailVos = BeanCopyUtils.copyBeanList(page.getRecords(), ArticleDetailVo.class);
        PageVo pageVo = new PageVo(articleDetailVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    @Transactional
    public ResponseResult add(AddArticleDto articleDto) {
        if (StringUtils.isEmpty(articleDto.getTitle())) {
            return ResponseResult.errorResult(444, "请输入标题");
        }

        if (StringUtils.isEmpty(articleDto.getCategoryId())) {
            return ResponseResult.errorResult(444, "请选择分类");
        }

        //添加 博客
        Article article = BeanCopyUtils.copyBean(articleDto, Article.class);
        article.setStatus("1");
        this.save(article);


        List<ArticleTag> articleTags = articleDto.getTags().stream()
                .map(tagId -> new ArticleTag(article.getId(), tagId))
                .collect(Collectors.toList());

        //添加 博客和标签的关联
        articleTagService.saveBatch(articleTags);
        return ResponseResult.okResult("发布成功");
    }

    @Override
    public ResponseResult pageArticleList(int pageNum, int pageSize, ArticleListDto articleListDto) {
        //分页查询
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(articleListDto.getTitle()), Article::getTitle, articleListDto.getTitle());
        queryWrapper.like(StringUtils.hasText(articleListDto.getSummary()), Article::getSummary, articleListDto.getSummary());
        queryWrapper.eq(Article::getStatus, 0).or().eq(Article::getStatus, 1);

        Page<Article> page = new Page<>(pageNum,pageSize);
        page(page, queryWrapper);
        //封装数据返回
        PageVo pageVo = new PageVo(page.getRecords(),page.getTotal());
        return ResponseResult.okResult(pageVo);
    }


    @Autowired
    private ArticleTagMapper articleTagMapper;
    @Override
    public ResponseResult getArticle(Long id) {
        Article article = articleMapper.selectById(id);
        List<Long> tagIdList = articleTagMapper.selectTagId(id);
        AddArticleDto addArticleDto = BeanCopyUtils.copyBean(article, AddArticleDto.class);
        addArticleDto.setTags(tagIdList);
        return ResponseResult.okResult(addArticleDto);
    }



}

