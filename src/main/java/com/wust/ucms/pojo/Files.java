package com.wust.ucms.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("files")
public class Files {
    @TableId
    private Integer id;
    private String fileName;
    private String src;
    private Integer loginId;
    private Integer clubId;
    private Integer memberId;
    private Integer activityId;
    private Integer fundId;
    private Integer competitionId;
}
