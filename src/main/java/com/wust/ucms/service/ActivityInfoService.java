package com.wust.ucms.service;

import com.wust.ucms.pojo.ActivityInfo;

import java.util.List;
import java.util.Map;

public interface ActivityInfoService {
    Integer createActivityInfo(ActivityInfo activityInfo);
    Integer deleteActivityInfo(Integer activityId);
    Integer updateActivityInfo(ActivityInfo activityInfo);
    ActivityInfo researchDetailActivityInfo(Integer activityId);
    // TODO 将statusCode作为queryParams的一部分传入，statusCode在controller层赋值，不由前端传递
    Map<String, Object> researchBasicActivityInfo(ActivityInfo activityInfo);
}
