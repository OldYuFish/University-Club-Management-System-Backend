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
    private Integer roleId;
}
