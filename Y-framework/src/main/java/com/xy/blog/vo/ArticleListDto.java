package com.xy.blog.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleListDto {
    private Long id;
    //标题
    private String title;
    //文章摘要
    private String summary;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
