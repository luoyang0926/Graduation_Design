package com.xy.blog.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVo {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    //分类名
    private String name;
     //状态0:正常,1禁用
   // private String status;
}
