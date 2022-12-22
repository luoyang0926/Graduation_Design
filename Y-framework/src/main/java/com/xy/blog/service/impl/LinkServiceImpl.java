package com.xy.blog.service.impl;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.blog.entity.Link;
import com.xy.blog.mapper.LinkMapper;
import com.xy.blog.service.LinkService;
import com.xy.blog.utils.BeanCopyUtils;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.utils.SystemConstants;
import com.xy.blog.vo.LinkVo;
import com.xy.blog.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 友链(Link)表服务实现类
 *
 * @author makejava
 * @since 2022-11-23 14:46:08
 */
@Service
public class LinkServiceImpl extends ServiceImpl<LinkMapper,Link> implements LinkService {


    @Autowired
    private LinkMapper linkMapper;


    @Override
    public ResponseResult getAllLink() {

        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Link::getStatus, SystemConstants.LINK_STATUS_NORMAL);
        List<Link> linkList = linkMapper.selectList(queryWrapper);
        List<LinkVo> linkVos = BeanCopyUtils.copyBeanList(linkList, LinkVo.class);
        return ResponseResult.okResult(linkVos);
    }

    @Override
    public ResponseResult<PageVo> pageLinkList(int pageNum, int pageSize, LinkVo linkVo) {
        LambdaQueryWrapper<Link> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(linkVo.getName()), Link::getName, linkVo.getName());
        queryWrapper.like(StringUtils.hasText(linkVo.getStatus()), Link::getStatus, linkVo.getStatus());
        queryWrapper.eq(Link::getStatus, 0);
        Page<Link> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        PageVo pageVo = new PageVo(page.getRecords(),page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getLink(Long id) {
        Link link = linkMapper.selectById(id);
        LinkVo linkVo = BeanCopyUtils.copyBean(link, LinkVo.class);
        return ResponseResult.okResult(linkVo);
    }

    @Override
    public ResponseResult add(Link link) {
        linkMapper.insert(link);
        return  ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateLink(Link link) {
        linkMapper.updateById(link);
        return ResponseResult.okResult();
    }
}

