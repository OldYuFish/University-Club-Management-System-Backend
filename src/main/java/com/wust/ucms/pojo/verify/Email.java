package com.wust.ucms.pojo.verify;

import lombok.Data;

@Data
public class Email {
    private String code;
    private Long saveTime;
    private Long updateTime;
}
