package com.xy.blog.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.xy.blog.entity.Article;
import com.xy.blog.entity.Category;
import com.xy.blog.enums.AppHttpCodeEnum;
import com.xy.blog.service.ArticleService;
import com.xy.blog.service.CategoryService;
import com.xy.blog.service.TagService;
import com.xy.blog.utils.BeanCopyUtils;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.utils.WebUtils;
import com.xy.blog.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TagService tagService;


    @PostMapping("/article")
    public ResponseResult add(@RequestBody AddArticleDto article){
        return articleService.add(article);
    }

    @PutMapping("/article")
    public ResponseResult update(@RequestBody AddArticleDto article){
        articleService.updateArticle(article);
        return ResponseResult.okResult();
    }

    @GetMapping("/article/list")
    public ResponseResult<PageVo> getArticleList(int pageSize,int pageNum,ArticleListDto articleListDto) {
        return articleService.pageArticleList(pageNum, pageSize, articleListDto);
    }

    @GetMapping("/article/{id}")
    public ResponseResult HXArticle(@PathVariable("id") Long id) {

        return articleService.getArticle(id);
    }


    @RequestMapping("/article/list")
    public ResponseResult articleList(int pageNum, int pageSize) {

        articleService.AdminArticleList(pageNum, pageSize);
        return ResponseResult.okResult();

    }

    /**
     * 新增类别
     * @param category
     * @return
     */
    @PostMapping("/category")
    public ResponseResult addCategory(@RequestBody Category category) {
        categoryService.add(category);
        return ResponseResult.okResult();
    }

    @GetMapping("/category/listAllCategory")
    public ResponseResult getAllCategory() {
        return   categoryService.getCategoryList();

    }

    /**
     * 回显
     * @param id
     * @return
     */
    @GetMapping("/category/{id}")
    public ResponseResult HXTag(@PathVariable("id") Long id) {

        return categoryService.selectCategory(id);
    }

    @GetMapping("/tag/listAllTag")
    public ResponseResult getAllTag() {
        return tagService.getTagList();

    }

    /**
     * 查询类别并分页
     * @param pageNum
     * @param pageSize
     * @param categoryListDto
     * @return
     */
    @RequestMapping("/category/list")
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, CategoryListDto categoryListDto){
        return categoryService.pageCategoryList(pageNum, pageSize, categoryListDto);
    }

    /**
     * 修改类别
     * @param category
     * @return
     */
    @PutMapping("/category")
    public ResponseResult updateCategory(@RequestBody Category  category) {
        categoryService.updateCategory(category);
        return ResponseResult.okResult();

    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @DeleteMapping("/category/{ids}")
    public ResponseResult deleteCategory(@PathVariable("ids") List<Long> ids) {
        categoryService.removeByIds(ids);
        return ResponseResult.okResult();

    }

    @DeleteMapping("/article/{ids}")
    public ResponseResult deleteArticle(@PathVariable("ids") List<Long> ids) {
        articleService.removeByIds(ids);
        return ResponseResult.okResult();

    }

    @PreAuthorize("@ps.hasPermission('content:category:export')")
    @GetMapping("/category/export")
    public void export(HttpServletResponse response) {
        try {
            //设置下载文件的请求头
            WebUtils.setDownLoadHeader("分类.xlsx",response);
            //获取需要导出的数据
            List<Category> categoryVos = categoryService.list();

            List<ExcelCategoryVo> excelCategoryVos = BeanCopyUtils.copyBeanList(categoryVos, ExcelCategoryVo.class);
            //把数据写入到Excel中
            EasyExcel.write(response.getOutputStream(), ExcelCategoryVo.class).autoCloseStream(Boolean.FALSE).sheet("分类导出")
                    .doWrite(excelCategoryVos);

        } catch (Exception e) {
            //如果出现异常也要响应json
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
            WebUtils.renderString(response, JSON.toJSONString(result));
        }
    }

}
