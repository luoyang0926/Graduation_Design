package com.xy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.blog.entity.SysUser;
import com.xy.blog.entity.Tag;
import com.xy.blog.mapper.TagMapper;
import com.xy.blog.service.TagService;
import com.xy.blog.utils.BeanCopyUtils;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.utils.SecurityUtils;
import com.xy.blog.vo.PageVo;
import com.xy.blog.vo.TagListDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 标签(Tag)表服务实现类
 *
 * @author makejava
 * @since 2022-11-26 16:58:57
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private TagService tagService;

    @Override
    public ResponseResult<PageVo> pageTagList(Integer pageNum, Integer pageSize, TagListDto tagListDto) {
        //分页查询
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(tagListDto.getName()),Tag::getName,tagListDto.getName());
        queryWrapper.eq(StringUtils.hasText(tagListDto.getRemark()),Tag::getRemark,tagListDto.getRemark());

        Page<Tag> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page, queryWrapper);
        //封装数据返回
        PageVo pageVo = new PageVo(page.getRecords(),page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult addTag(Tag tag) {
       /* Long userId = SecurityUtils.getUserId();
        tag.setCreateBy(userId);*/
        tagMapper.insert(tag);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult selectTag(Long id) {
        Tag tag = tagMapper.selectById(id);
        return ResponseResult.okResult(tag);
    }

    @Override
    public ResponseResult updateTag(Tag tag) {

        Long userId = SecurityUtils.getUserId();
        tag.setUpdateBy(userId);
        //tagMapper.updateTagById(tag);
        LambdaUpdateWrapper<Tag> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(Tag::getId, tag.getId());
        tagMapper.update(tag, updateWrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getTagList() {
       LambdaQueryWrapper<Tag> queryWrapper=new LambdaQueryWrapper<>();
        List<Tag> tagList = tagService.list(queryWrapper);
        List<TagListDto> tagListDtos = BeanCopyUtils.copyBeanList(tagList, TagListDto.class);
        return ResponseResult.okResult(tagListDtos);

    }
}

