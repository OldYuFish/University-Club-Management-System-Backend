package com.wust.ucms.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("role_page")
public class RolePage {
    @TableId
    private Integer id;
    private Integer roleId;
    private Integer pageId;
}
