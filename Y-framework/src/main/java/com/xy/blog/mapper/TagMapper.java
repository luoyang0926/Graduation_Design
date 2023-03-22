package com.xy.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.blog.entity.Tag;
import org.springframework.stereotype.Repository;


/**
 * 标签(Tag)表数据库访问层
 *
 * @author makejava
 * @since 2022-11-26 16:58:57
 */
@Repository
public interface TagMapper extends BaseMapper<Tag> {
    void updateTagById(Tag tag);
}

