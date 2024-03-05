package com.wust.ucms.service;

import com.wust.ucms.pojo.ActivityInfo;

import java.util.List;
import java.util.Map;

public interface ActivityInfoService {
    Integer createActivityInfo(ActivityInfo activityInfo);
    Integer deleteActivityInfo(Integer activityId);
    Integer updateActivityInfo(ActivityInfo activityInfo);
    ActivityInfo researchDetailActivityInfo(Integer activityId);
    Map<String, Object> researchBasicActivityInfo(ActivityInfo activityInfo);
}
