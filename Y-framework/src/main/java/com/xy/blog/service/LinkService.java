package com.xy.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.blog.entity.Link;
import com.xy.blog.utils.ResponseResult;
import com.xy.blog.vo.LinkVo;
import com.xy.blog.vo.PageVo;


/**
 * 友链(Link)表服务接口
 *
 * @author makejava
 * @since 2022-11-23 14:46:08
 */
public interface LinkService extends IService<Link> {

    ResponseResult getAllLink();

    ResponseResult<PageVo> pageLinkList(int pageNum, int pageSize, LinkVo linkVo);

    ResponseResult getLink(Long id);

    ResponseResult add(Link link);

    ResponseResult updateLink(Link link);
}
