package com.xy.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtdArticleVo {
    private Long id;
    //标题
    private String title;

    //访问量
    private Long viewCount;

    private Date createTime;
}
