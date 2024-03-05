package com.wust.ucms.controller;

import com.wust.ucms.controller.utils.Result;
import com.wust.ucms.pojo.ClubInfo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/club")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ClubController {

    @PostMapping("/create")
    public Result createClub(@RequestBody ClubInfo clubInfo) {
        return null;
    }

    @PostMapping("/delete")
    public Result deleteClub(@RequestBody ClubInfo clubInfo) {
        return null;
    }

    @PostMapping("/update")
    public Result updateClub(@RequestBody ClubInfo clubInfo) {
        return null;
    }

    @PostMapping("/approval")
    public Result approvalClub(@RequestBody ClubInfo clubInfo) {
        return null;
    }

    @PostMapping("/research/detail")
    public Result researchDetailClub(@RequestBody ClubInfo clubInfo) {
        return null;
    }

    @PostMapping("/research/not-submit")
    public Result researchNotSubmitClub(@RequestBody ClubInfo clubInfo) {
        return null;
    }

    @PostMapping("/research/not-approval")
    public Result researchNotApprovalClub(@RequestBody ClubInfo clubInfo) {
        return null;
    }

    @PostMapping("/research/been-rejected")
    public Result researchBeenRejectedClub(@RequestBody ClubInfo clubInfo) {
        return null;
    }

    @PostMapping("/research/been-accepted")
    public Result researchBeenAcceptedClub(@RequestBody ClubInfo clubInfo) {
        return null;
    }
}
