package com.wust.ucms.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("login_info")
public class LoginInfo {
    @TableId
    private Integer id;
    private String realName;
    @TableField("psd")
    private String password;
    private String studentNumber;
    private String teacherNumber;
    private String phone;
    private String email;
    private Integer isDelete;
    private Integer roleId;
    @TableField(exist = false)
    private String oldPassword;
    @TableField(exist = false)
    private String verifyCode;
    @TableField(exist = false)
    private String token;
    @TableField(exist = false)
    private String roleName;
    @TableField(exist = false)
    private Long pageIndex;
    @TableField(exist = false)
    private Long pageSize;
}
