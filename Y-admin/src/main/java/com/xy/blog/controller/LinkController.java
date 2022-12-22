package com.xy.blog.controller;

import com.xy.blog.entity.Link;
import com.xy.blog.service.LinkService;
import com.xy.blog.service.TagService;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.vo.LinkVo;
import com.xy.blog.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @PostMapping
    public ResponseResult addLink(@RequestBody Link link) {
        linkService.add(link);
        return ResponseResult.okResult();
    }
    @RequestMapping("/list")
    public ResponseResult<PageVo> pageLinkList(int pageNum , int pageSize, LinkVo linkVo) {
        return linkService.pageLinkList(pageNum,pageSize,linkVo);
    }

    @GetMapping("/{id}")
    public ResponseResult HXLink(@PathVariable("id") Long id) {
        return linkService.getLink(id);
    }

    @DeleteMapping("/{ids}")
    public ResponseResult deleteLink(@PathVariable("ids") List<Long> ids) {
         linkService.removeByIds(ids);
        return ResponseResult.okResult();
    }

    @PutMapping
    public ResponseResult updateLink(@RequestBody Link link) {
        linkService.updateLink(link);
        return ResponseResult.okResult();
    }
}
