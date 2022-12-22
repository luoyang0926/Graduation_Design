package com.xy.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDetailVo {

    private String  content;
    private Long    categoryId;
    private String  categoryName;
    private Date    createTime;
    private Integer delFlag;
    private Long    id;
    private String  isComment;
    private String  isTop;
    private String  status;
    private String  summary;
    private String  thumbnail;
    private String  title;
    private Long    viewCount;
}
