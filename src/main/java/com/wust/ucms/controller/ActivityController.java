package com.wust.ucms.controller;

import com.wust.ucms.controller.utils.Result;
import com.wust.ucms.pojo.ActivityInfo;
import com.wust.ucms.service.impl.ActivityInfoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/activity")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ActivityController {

    @Autowired
    ActivityInfoServiceImpl activity;

    private static Integer paramsException(ActivityInfo activityInfo) {
        try {
            if (activityInfo.getTitle() == null || activityInfo.getTitle().isEmpty() ||
                    activityInfo.getType() == null || activityInfo.getType().isEmpty() ||
                    activityInfo.getAddress() == null || activityInfo.getAddress().isEmpty() ||
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
                    activityInfo.getBudget().compareTo(BigDecimal.ZERO) < 0 ||
                    activityInfo.getBudget().compareTo(new BigDecimal("99999999.99")) > 0 ||
                    activityInfo.getOutput().compareTo(BigDecimal.ZERO) < 0 ||
                    activityInfo.getOutput().compareTo(new BigDecimal("99999999.99")) > 0 ||
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
            if (activityInfo.getTitle() == null || activityInfo.getTitle().isEmpty() ||
                    activityInfo.getType() == null || activityInfo.getType().isEmpty() ||
                    activityInfo.getClubName() == null || activityInfo.getClubName().isEmpty() ||
                    activityInfo.getPageIndex() == null ||
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
    public Result createActivity(@RequestBody ActivityInfo activityInfo) {
        Integer code = paramsException(activityInfo);
        if (code != 0) return new Result(code);

        try {
            if ((activityInfo.getUseFund() == 0 && !Objects.equals(activityInfo.getBudget(), BigDecimal.ZERO)) ||
                    (activityInfo.getUseFund() != 0 && Objects.equals(activityInfo.getBudget(), BigDecimal.ZERO)) ||
                    (activityInfo.getStatusCode() != 0 && activityInfo.getStatusCode() != 1) ||
                    !Objects.equals(activityInfo.getOutput(), BigDecimal.ZERO) ||
                    (activityInfo.getRealNumber() != null && activityInfo.getRealNumber() != 0) ||
                    (activityInfo.getSummarize() != null && activityInfo.getSummarize().isEmpty()) ||
                    (activityInfo.getApprovalComment() != null && activityInfo.getApprovalComment().isEmpty()) ||
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

        return new Result(0, data);
    }

    @PostMapping("/delete")
    public Result deleteActivity(@RequestBody ActivityInfo activityInfo) {
        Integer id = activityInfo.getId();
        try {
            if (id == null || id <= 0) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        Integer code = activity.deleteActivityInfo(id);
        return new Result(code);
    }

    @PostMapping("/update")
    public Result updateActivity(@RequestBody ActivityInfo activityInfo) {
        Integer code = paramsException(activityInfo);
        if (code != 0) return new Result(code);

        try {
            if (activityInfo.getId() == null || activityInfo.getId() <= 0 ||
                    (activityInfo.getUseFund() == 0 && !Objects.equals(activityInfo.getBudget(), BigDecimal.ZERO)) ||
                    (activityInfo.getUseFund() != 0 && Objects.equals(activityInfo.getBudget(), BigDecimal.ZERO)) ||
                    (activityInfo.getStatusCode() != 3 &&
                            (!Objects.equals(activityInfo.getOutput(), BigDecimal.ZERO) ||
                                    (activityInfo.getRealNumber() != null && activityInfo.getRealNumber() != 0) ||
                                    (activityInfo.getSummarize() != null && activityInfo.getSummarize().isEmpty()))) ||
                    (activityInfo.getStatusCode() !=2 &&
                            (activityInfo.getApprovalComment() != null && activityInfo.getApprovalComment().isEmpty())) ||
                    (activityInfo.getStatusCode() ==2 &&
                            (activityInfo.getApprovalComment() == null || !activityInfo.getApprovalComment().isEmpty())) ||
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

        return new Result(0, data);
    }

    @PostMapping("/approval")
    public Result approvalActivity(@RequestBody ActivityInfo activityInfo) {
        Integer id = activityInfo.getId();
        try {
            if (id == null || id <= 0) throw new Exception("参数逻辑错误！");
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
