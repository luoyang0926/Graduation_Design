package com.xy.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.blog.entity.Tag;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.vo.PageVo;
import com.xy.blog.vo.TagListDto;


/**
 * 标签(Tag)表服务接口
 *
 * @author makejava
 * @since 2022-11-26 16:58:57
 */
public interface TagService extends IService<Tag> {

    ResponseResult<PageVo> pageTagList(Integer pageNum, Integer pageSize, TagListDto tagListDto);

    ResponseResult addTag(Tag tag);

    ResponseResult selectTag(Long id);

    ResponseResult updateTag(Tag tag);


    ResponseResult getTagList();

}
