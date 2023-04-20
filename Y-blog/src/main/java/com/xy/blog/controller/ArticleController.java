package com.xy.blog.controller;

import com.xy.blog.service.ArticleService;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.vo.ArticleParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

   /* @GetMapping("/list")
    public List<Article> test(){
        return articleService.list();
    }*/

    @GetMapping("/hotArticleList")
    public ResponseResult hotArticleList() {
        return articleService.hotArticleList();
    }

    @GetMapping("/utDArticleList")
    public ResponseResult utdArticleList() {
        return articleService.utDArticleList();
    }

    @GetMapping("/articleList")
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        return articleService.articleList(pageNum, pageSize, categoryId);
    }

    @GetMapping("/{id}")
    public ResponseResult DetilsArticle(@PathVariable("id") Long id) {
        return articleService.getArticleById(id);
    }

    @PutMapping("/updateViewCount/{id}")
    public ResponseResult updateViewCount(@PathVariable("id") Long articleId) {
        return articleService.updateViewCount(articleId);
    }

    @GetMapping("/getMyArticleTotal")
    public ResponseResult getMyArticleTotal(HttpServletRequest request) {

        return articleService.getMyArticleTotal(request);
    }

    @GetMapping("/getTotalView")
    public ResponseResult getTotalView(HttpServletRequest request) {
        return articleService.getTotalView(request);

    }

    @GetMapping("/myArticleList")
    public ResponseResult getMyArticleList(Integer pageNum, Integer pageSize, Long categoryId,Long uid) {
     return    articleService.getMyArticleList(pageNum, pageSize, categoryId,uid);
    }

    @PostMapping("/search")
    public Object search(@RequestBody ArticleParam articleParam) {
        //搜索接口
        String search = articleParam.getSearch();
        return  articleService.getArticleBySearch(search);

    }
}
