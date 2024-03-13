package com.wust.ucms.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("page_permission")
public class PagePermission {
    @TableId
    private Integer id;
    private Integer pageId;
    private Integer permissionId;
}
