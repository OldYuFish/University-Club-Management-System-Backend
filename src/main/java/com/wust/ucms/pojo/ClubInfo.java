package com.wust.ucms.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("club_info")
public class ClubInfo {
    @TableId
    private Integer id;
    private String clubName;
    private Integer membersNumber;
    private String type;
    private String clubLevel;
    private String department;
    private BigDecimal totalFund;
    private BigDecimal surplusFund;
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applicationTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date establishmentTime;
    private Integer statusCode;
    private String approvalComment;
    private Integer loginId;
    @TableField(exist = false)
    private String realName;
    @TableField(exist = false)
    private String studentNumber;
    @TableField(exist = false)
    private String email;
    @TableField(exist = false)
    private Long pageIndex;
    @TableField(exist = false)
    private Long pageSize;
}
