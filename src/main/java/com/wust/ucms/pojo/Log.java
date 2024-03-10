package com.wust.ucms.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@TableName("log")
public class Log {
    @TableId
    private Integer id;
    private String object;
    private String operate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime;
    private Integer loginId;
    @TableField(exist = false)
    private String studentNumber;
    @TableField(exist = false)
    private String teacherNumber;
    @TableField(exist = false)
    private Long pageIndex;
    @TableField(exist = false)
    private Long pageSize;
}
