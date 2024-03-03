package com.wust.ucms.service.impl;

import com.wust.ucms.mapper.ActivityInfoMapper;
import com.wust.ucms.pojo.ActivityInfo;
import com.wust.ucms.service.ActivityInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ActivityInfoServiceImpl implements ActivityInfoService {

    @Autowired
    ActivityInfoMapper activity;

    @Override
    public Integer createActivityInfo(ActivityInfo activityInfo) {
        return null;
    }

    @Override
    public Integer deleteActivityInfo(ActivityInfo activityInfo) {
        return null;
    }

    @Override
    public Integer updateActivityInfo(ActivityInfo activityInfo) {
        return null;
    }

    @Override
    public ActivityInfo researchDetailActivityInfo(Integer activityId) {
        return null;
    }

    @Override
    public List<Map<String, Object>> researchBasicActivityInfo(ActivityInfo activityInfo) {
        return null;
    }
}
