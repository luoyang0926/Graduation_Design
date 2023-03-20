package com.xy.blog.controller;

import com.xy.blog.entity.Tag;
import com.xy.blog.service.TagService;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.vo.PageVo;
import com.xy.blog.vo.TagListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content/tag")
public class TagController {
    @Autowired
    private TagService tagService;

    @PostMapping
    public ResponseResult addTag(@RequestBody Tag  tag) {
        tagService.addTag(tag);
        return ResponseResult.okResult();

    }

    @RequestMapping("/list")
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, TagListDto tagListDto){
        return tagService.pageTagList(pageNum,pageSize,tagListDto);
    }

    @GetMapping("/{id}")
    public ResponseResult HXTag(@PathVariable("id") Long id) {

        return tagService.selectTag(id);


    }

    @PutMapping
    public ResponseResult updateTag(@RequestBody Tag  tag) {
        tagService.updateTag(tag);
        return ResponseResult.okResult();

    }

    @DeleteMapping("/{ids}")
    public ResponseResult deleteTag(@PathVariable("ids") List<Long> ids) {
        tagService.removeByIds(ids);
        return ResponseResult.okResult();

    }



}
