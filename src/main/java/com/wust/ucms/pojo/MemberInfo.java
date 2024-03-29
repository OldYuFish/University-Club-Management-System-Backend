package com.wust.ucms.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("member_info")
public class MemberInfo {
    @TableId
    private Integer id;
    private String memberName;
    private String department;
    private String job;
    private String studentNumber;
    private String email;
    private String honor;
    private String description;
    private Integer clubId;
}
