package com.wust.ucms.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("competition_bonus")
public class CompetitionBonus {
    @TableId
    private Integer id;
    private String competitionName;
    private String type;
    private String competitionLevel;
    private String award;
}
