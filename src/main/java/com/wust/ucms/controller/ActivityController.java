package com.wust.ucms.controller;

import com.wust.ucms.controller.utils.Result;
import com.wust.ucms.pojo.ActivityInfo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ActivityController {

    @PostMapping("/create")
    public Result createActivity(@RequestBody ActivityInfo activityInfo) {
        return null;
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
