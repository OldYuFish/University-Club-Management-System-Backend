package com.wust.ucms.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName("user_role")
public class UserRole {
    @TableId
    private Integer id;
    private String roleName;
    @TableField(exist = false)
    private List<Permission> permissionList;
    @TableField(exist = false)
    private Long pageIndex;
    @TableField(exist = false)
    private Long pageSize;
}
