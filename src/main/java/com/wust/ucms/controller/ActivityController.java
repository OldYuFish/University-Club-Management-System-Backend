package com.wust.ucms.controller;

import com.wust.ucms.controller.utils.Result;
import com.wust.ucms.pojo.ActivityInfo;
import com.wust.ucms.pojo.RSAKeyProperties;
import com.wust.ucms.service.impl.ActivityInfoServiceImpl;
import com.wust.ucms.service.impl.LogServiceImpl;
import com.wust.ucms.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/activity")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ActivityController {

    @Autowired
    ActivityInfoServiceImpl activity;

    @Autowired
    LogServiceImpl log;

    @Autowired
    RSAKeyProperties rsaKeyProperties;

    private static Integer paramsException(ActivityInfo activityInfo) {
        try {
            if (!StringUtils.hasText(activityInfo.getTitle()) ||
                    !StringUtils.hasText(activityInfo.getType()) ||
                    !StringUtils.hasText(activityInfo.getAddress()) ||
                    activityInfo.getActivityStartTime() == null ||
                    activityInfo.getActivityEndTime() == null ||
                    activityInfo.getStatusCode() == null ||
                    activityInfo.getClubId() == null
            ) throw new Exception("缺少参数！");
        } catch (Exception e) {
            return -20001;
        }

        try {
            if (activityInfo.getTitle().length() > 36 ||
                    activityInfo.getType().length() > 12 ||
                    activityInfo.getAddress().length() > 36
            ) throw new Exception("参数格式错误！");
        } catch (Exception e) {
            return -20002;
        }

        return 0;
    }

    private static Integer queryParamsException(ActivityInfo activityInfo) {
        try {
            if (activityInfo.getPageIndex() == null ||
                    activityInfo.getPageSize() == null
            ) throw new Exception("缺少参数！");
        } catch (Exception e) {
            return -20001;
        }

        try {
            if (activityInfo.getTitle().length() > 36 ||
                    activityInfo.getType().length() > 12 ||
                    activityInfo.getClubName().length() > 24 ||
                    activityInfo.getPageIndex() <= 0 ||
                    activityInfo.getPageSize() <= 0
            ) throw new Exception("参数格式错误！");
        } catch (Exception e) {
            return -20002;
        }

        return 0;
    }

    @PostMapping("/create")
    public Result createActivity(@RequestHeader("Authorization") String token, @RequestBody ActivityInfo activityInfo) throws Exception {
        Integer code = paramsException(activityInfo);
        if (code != 0) return new Result(code);

        try {
            if ((activityInfo.getStatusCode() != 0 && activityInfo.getStatusCode() != 1) ||
                    (activityInfo.getRealNumber() != null && activityInfo.getRealNumber() != 0) ||
                    StringUtils.hasText(activityInfo.getSummarize()) ||
                    StringUtils.hasText(activityInfo.getApprovalComment()) ||
                    (activityInfo.getShouldApply() == 0 &&
                            (activityInfo.getApplicationStartTime() != null && activityInfo.getApplicationEndTime() != null)) ||
                    (activityInfo.getShouldApply() == 1 &&
                            (activityInfo.getApplicationStartTime() == null || activityInfo.getApplicationEndTime() == null))
            ) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        code = activity.createActivityInfo(activityInfo);
        if (code <= 0) return new Result(code);
        Map<String, Object> data = new HashMap<>();
        data.put("id", code);

        log.createLog("活动申请表："+code, "创建", JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(0, data);
    }

    @PostMapping("/delete")
    public Result deleteActivity(@RequestHeader("Authorization") String token, @RequestBody ActivityInfo activityInfo) throws Exception {
        Integer id = activityInfo.getId();
        try {
            if (id == null || id <= 0 ||
                    activity.researchDetailActivityInfo(id).getStatusCode() == 3
            ) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        Integer code = activity.deleteActivityInfo(id);

        log.createLog("活动申请表："+id, "删除", JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(code);
    }

    @PostMapping("/update")
    public Result updateActivity(@RequestHeader("Authorization") String token, @RequestBody ActivityInfo activityInfo) throws Exception {
        Integer code = paramsException(activityInfo);
        if (code != 0) return new Result(code);

        try {
            if (activityInfo.getId() == null || activityInfo.getId() <= 0 ||
                    (activityInfo.getStatusCode() != 3 &&
                            ((activityInfo.getRealNumber() != null && activityInfo.getRealNumber() != 0) ||
                                    StringUtils.hasText(activityInfo.getSummarize()))) ||
                    ((activityInfo.getStatusCode() == 0 || activityInfo.getStatusCode() == 1) &&
                            StringUtils.hasText(activityInfo.getApprovalComment())) ||
                    (activityInfo.getStatusCode() ==2 &&
                            !StringUtils.hasText(activityInfo.getApprovalComment())) ||
                    (activityInfo.getShouldApply() == 0 &&
                            (activityInfo.getApplicationStartTime() != null && activityInfo.getApplicationEndTime() != null)) ||
                    (activityInfo.getShouldApply() == 1 &&
                            (activityInfo.getApplicationStartTime() == null || activityInfo.getApplicationEndTime() == null))
            ) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        code = activity.updateActivityInfo(activityInfo);
        if (code <= 0) return new Result(code);
        Map<String, Object> data = new HashMap<>();
        data.put("id", code);

        log.createLog("活动申请表："+code, "修改", JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(0, data);
    }

    @PostMapping("/approval")
    public Result approvalActivity(@RequestHeader("Authorization") String token, @RequestBody ActivityInfo activityInfo) throws Exception {
        Integer id = activityInfo.getId();
        try {
            if (id == null || id <= 0 ||
                    (activityInfo.getStatusCode() == 2 &&
                            !StringUtils.hasText(activityInfo.getApprovalComment())) ||
                    activityInfo.getStatusCode() == 0 || activityInfo.getStatusCode() == 1
            ) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        ActivityInfo activityInformation = activity.researchDetailActivityInfo(id);
        if (activityInformation == null) return new Result(-20003);
        activityInformation.setStatusCode(activityInfo.getStatusCode());
        activityInformation.setApprovalComment(activityInfo.getApprovalComment());

        Integer code = activity.updateActivityInfo(activityInformation);
        if (code <= 0) return new Result(code);
        Map<String, Object> data = new HashMap<>();
        data.put("id", code);

        log.createLog("活动申请表："+code, "审批", JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(0, data);
    }

    @PostMapping("/research/detail")
    public Result researchDetailActivity(@RequestBody ActivityInfo activityInfo) {
        Integer id = activityInfo.getId();
        try {
            if (id == null || id <= 0) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        activityInfo = activity.researchDetailActivityInfo(id);
        if (activityInfo == null) return new Result(-20003);

        Map<String, Object> data = new HashMap<>();
        data.put("activityInfo", activityInfo);

        return new Result(0, data);
    }

    @PostMapping("/research/not-submit")
    public Result researchNotSubmitActivity(@RequestBody ActivityInfo activityInfo) {
        Integer code = queryParamsException(activityInfo);
        if (code != 0) return new Result(code);

        activityInfo.setStatusCode(0);
        Map<String, Object> data = activity.researchBasicActivityInfo(activityInfo);

        return new Result(0, data);
    }

    @PostMapping("/research/not-approval")
    public Result researchNotApprovalActivity(@RequestBody ActivityInfo activityInfo) {
        Integer code = queryParamsException(activityInfo);
        if (code != 0) return new Result(code);

        activityInfo.setStatusCode(1);
        Map<String, Object> data = activity.researchBasicActivityInfo(activityInfo);

        return new Result(0, data);
    }

    @PostMapping("/research/been-rejected")
    public Result researchBeenRejectedActivity(@RequestBody ActivityInfo activityInfo) {
        Integer code = queryParamsException(activityInfo);
        if (code != 0) return new Result(code);

        activityInfo.setStatusCode(2);
        Map<String, Object> data = activity.researchBasicActivityInfo(activityInfo);

        return new Result(0, data);
    }

    @PostMapping("/research/been-accepted")
    public Result researchBeenAcceptedActivity(@RequestBody ActivityInfo activityInfo) {
        Integer code = queryParamsException(activityInfo);
        if (code != 0) return new Result(code);

        activityInfo.setStatusCode(3);
        Map<String, Object> data = activity.researchBasicActivityInfo(activityInfo);

        return new Result(0, data);
    }
}
