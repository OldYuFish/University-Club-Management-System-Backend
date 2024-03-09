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
@TableName("fund_info")
public class FundInfo {
    @TableId
    private Integer id;
    private String theme;
    private String type;
    private BigDecimal amount;
    private BigDecimal surplus;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date appropriationTime;
    private Integer statusCode;
    private String approvalComment;
    private Integer clubId;
    private Integer competitionId;
    private Integer activityId;
    @TableField(exist = false)
    private String clubName;
    @TableField(exist = false)
    private CompetitionBonus competitionBonus;
    @TableField(exist = false)
    private String title;
    @TableField(exist = false)
    private Long pageIndex;
    @TableField(exist = false)
    private Long pageSize;
}
