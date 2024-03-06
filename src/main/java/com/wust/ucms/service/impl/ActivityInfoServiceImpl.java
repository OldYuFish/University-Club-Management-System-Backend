package com.wust.ucms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wust.ucms.mapper.ActivityInfoMapper;
import com.wust.ucms.mapper.ClubInfoMapper;
import com.wust.ucms.pojo.ActivityInfo;
import com.wust.ucms.service.ActivityInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ActivityInfoServiceImpl implements ActivityInfoService {

    @Autowired
    ActivityInfoMapper activity;

    @Autowired
    ClubInfoMapper club;

    @Override
    public Integer createActivityInfo(ActivityInfo activityInfo) {
        int flag = activity.insert(activityInfo);
        if (flag > 0) return activityInfo.getId();

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public Integer deleteActivityInfo(Integer activityId) {
        int flag = activity.deleteById(activityId);
        if (flag > 0) return 0;

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public Integer updateActivityInfo(ActivityInfo activityInfo) {
        String title = activityInfo.getTitle();
        String coOrganizer = activityInfo.getCoOrganizer();
        String type = activityInfo.getType();
        String address = activityInfo.getAddress();
        Date activityStartTime = activityInfo.getActivityStartTime();
        Date activityEndTime = activityInfo.getActivityEndTime();
        Integer shouldApply = activityInfo.getShouldApply();
        Date applicationStartTime = activityInfo.getApplicationStartTime();
        Date applicationEndTime = activityInfo.getApplicationEndTime();
        Integer numberLimit = activityInfo.getNumberLimit();
        Integer realNumber = activityInfo.getRealNumber();
        String description = activityInfo.getDescription();
        String summarize = activityInfo.getSummarize();
        Integer statusCode = activityInfo.getStatusCode();
        String approvalComment = activityInfo.getApprovalComment();
        Integer clubId = activityInfo.getClubId();

        activityInfo = activity.selectById(activityInfo.getId());

        activityInfo.setTitle(title);
        activityInfo.setCoOrganizer(coOrganizer);
        activityInfo.setType(type);
        activityInfo.setAddress(address);
        activityInfo.setActivityStartTime(activityStartTime);
        activityInfo.setActivityEndTime(activityEndTime);
        activityInfo.setShouldApply(shouldApply);
        activityInfo.setApplicationStartTime(applicationStartTime);
        activityInfo.setApplicationEndTime(applicationEndTime);
        activityInfo.setNumberLimit(numberLimit);
        activityInfo.setRealNumber(realNumber);
        activityInfo.setDescription(description);
        activityInfo.setSummarize(summarize);
        activityInfo.setStatusCode(statusCode);
        activityInfo.setApprovalComment(approvalComment);
        activityInfo.setClubId(clubId);

        int flag = activity.updateById(activityInfo);
        if (flag > 0) return activityInfo.getId();

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public ActivityInfo researchDetailActivityInfo(Integer activityId) {
        ActivityInfo activityInfo = activity.selectById(activityId);
        activityInfo.setClubName(club.selectById(activityInfo.getClubId()).getClubName());
        return activityInfo;
    }

    @Override
    public Map<String, Object> researchBasicActivityInfo(ActivityInfo activityInfo) {
        LambdaQueryWrapper<ActivityInfo> lqw = new LambdaQueryWrapper<>();
        lqw.like(
                activityInfo.getTitle() != null && !activityInfo.getTitle().isEmpty(),
                ActivityInfo::getTitle,
                activityInfo.getTitle()
        ).eq(
                club.selectClubIdByClubName(activityInfo.getClubName()) != null,
                ActivityInfo::getClubId,
                club.selectClubIdByClubName(activityInfo.getClubName())
        ).eq(
                activityInfo.getType() != null && activityInfo.getType().isEmpty(),
                ActivityInfo::getType,
                activityInfo.getType()
        ).eq(
                activityInfo.getStatusCode() != null,
                ActivityInfo::getStatusCode,
                activityInfo.getStatusCode()
        );

        IPage<ActivityInfo> page = new Page<>(activityInfo.getPageIndex(), activityInfo.getPageSize());
        activity.selectPage(page, lqw);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("total", page.getTotal());
        pagination.put("pageIndex", activityInfo.getPageIndex());
        pagination.put("pageSize", activityInfo.getPageSize());

        List<Map<String, Object>> activityList = new ArrayList<>();
        for (ActivityInfo a: page.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("title", a.getTitle());
            map.put("clubName", club.selectById(a.getClubId()).getClubName());
            map.put("type", a.getType());
            map.put("numberLimit", a.getNumberLimit() == 0 ? "--" : a.getNumberLimit());
            map.put("statusCode", a.getStatusCode());
            activityList.add(map);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("activityList", activityList);
        data.put("pagination", pagination);

        return data;
    }
}
