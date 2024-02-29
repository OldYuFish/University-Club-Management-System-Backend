package com.wust.ucms.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("page")
public class Page {
    @TableId
    private Integer id;
    private String pageName;
    private String url;
}
