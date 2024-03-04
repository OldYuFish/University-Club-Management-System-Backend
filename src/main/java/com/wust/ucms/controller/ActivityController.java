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

    @PostMapping("/create")
    public Result createActivity(@RequestBody ActivityInfo activityInfo) {
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
            return new Result(-20001);
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
            return new Result(-20002);
        }

        try {
            if ((activityInfo.getUseFund() == 0 && !Objects.equals(activityInfo.getBudget(), BigDecimal.ZERO)) ||
                    (activityInfo.getUseFund() != 0 && Objects.equals(activityInfo.getBudget(), BigDecimal.ZERO)) ||
                    (activityInfo.getStatusCode() != 0 && activityInfo.getStatusCode() != 1) ||
                    !Objects.equals(activityInfo.getOutput(), BigDecimal.ZERO) ||
                    (activityInfo.getRealNumber() != null && activityInfo.getRealNumber() != 0) ||
                    (activityInfo.getSummarize() != null && activityInfo.getSummarize().isEmpty()) ||
                    (activityInfo.getShouldApply() == 0 &&
                            (activityInfo.getApplicationStartTime() != null || activityInfo.getApplicationEndTime() != null)) ||
                    (activityInfo.getShouldApply() == 1 &&
                            (activityInfo.getApplicationStartTime() == null || activityInfo.getApplicationEndTime() == null))
            ) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        Integer code = activity.createActivityInfo(activityInfo);
        if (code <= 0) return new Result(code);
        Map<String, Object> data = new HashMap<>();
        data.put("id", code);

        return new Result(0, data);
    }

    @PostMapping("/delete")
    public Result deleteActivity(@RequestBody ActivityInfo activityInfo) {
        return null;
    }

    @PostMapping("/update")
    public Result updateActivity(@RequestBody ActivityInfo activityInfo) {
        return null;
    }

    @PostMapping("/submit")
    public Result submitActivity(@RequestBody ActivityInfo activityInfo) {
        return null;
    }

    @PostMapping("/approval")
    public Result approvalActivity(@RequestBody ActivityInfo activityInfo) {
        return null;
    }

    @PostMapping("/research/detail")
    public Result researchDetailActivity(@RequestBody ActivityInfo activityInfo) {
        return null;
    }

    @PostMapping("/research/not-submit")
    public Result researchNotSubmitActivity() {
        return null;
    }

    @PostMapping("/research/not-approval")
    public Result researchNotApprovalActivity() {
        return null;
    }

    @PostMapping("/research/been-rejected")
    public Result researchBeenRejectedActivity() {
        return null;
    }

    @PostMapping("/research/been-accepted")
    public Result researchBeenAcceptedActivity() {
        return null;
    }
}
