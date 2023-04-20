package com.xy.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleParam {

    private Long id;
    //标题
    private String title;
    private Date createTime;
    private String search;
}
