package com.wust.ucms.controller;

import com.wust.ucms.controller.utils.Result;
import com.wust.ucms.pojo.ClubInfo;
import com.wust.ucms.pojo.RSAKeyProperties;
import com.wust.ucms.service.impl.ClubInfoServiceImpl;
import com.wust.ucms.service.impl.LogServiceImpl;
import com.wust.ucms.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/club")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ClubController {

    @Autowired
    ClubInfoServiceImpl club;

    @Autowired
    LogServiceImpl log;

    @Autowired
    RSAKeyProperties rsaKeyProperties;

    private static Integer paramsException(ClubInfo clubInfo) {
        try {
            if (!StringUtils.hasText(clubInfo.getClubName()) ||
                    !StringUtils.hasText(clubInfo.getType()) ||
                    clubInfo.getMembersNumber() == null ||
                    clubInfo.getApplicationTime() == null ||
                    clubInfo.getLoginId() == null
            ) throw new Exception("缺少参数！");
        } catch (Exception e) {
            return -20001;
        }

        try {
            if (clubInfo.getClubName().length() > 24 ||
                    clubInfo.getMembersNumber() <= 0 ||
                    clubInfo.getType().length() > 8 ||
                    clubInfo.getClubLevel().length() > 6 ||
                    clubInfo.getDepartment().length() > 24 ||
                    clubInfo.getTotalFund().compareTo(BigDecimal.ZERO) < 0 ||
                    clubInfo.getTotalFund().compareTo(new BigDecimal("99999999.99")) > 0 ||
                    clubInfo.getSurplusFund().compareTo(BigDecimal.ZERO) < 0 ||
                    clubInfo.getSurplusFund().compareTo(new BigDecimal("99999999.99")) > 0
            ) throw new Exception("参数格式错误！");
        } catch (Exception e) {
            return -20002;
        }

        return 0;
    }

    private static Integer queryParamsException(ClubInfo clubInfo) {
        try {
            if (clubInfo.getPageIndex() == null ||
                    clubInfo.getPageSize() == null
            ) throw new Exception("缺少参数！");
        } catch (Exception e) {
            return -20001;
        }

        try {
            if (clubInfo.getClubName().length() > 24 ||
                    clubInfo.getType().length() > 8 ||
                    clubInfo.getClubLevel().length() > 6 ||
                    clubInfo.getDepartment().length() > 24 ||
                    clubInfo.getPageIndex() <= 0 ||
                    clubInfo.getPageSize() <= 0
            ) throw new Exception("参数格式错误！");
        } catch (Exception e) {
            return -20002;
        }

        return 0;
    }

    @PostMapping("/create")
    public Result createClub(@RequestHeader("Authorization") String token, @RequestBody ClubInfo clubInfo) throws Exception {
        Integer code = paramsException(clubInfo);
        if (code != 0) return new Result(code);

        try {
            if (clubInfo.getTotalFund().compareTo(clubInfo.getSurplusFund()) < 0 ||
                    clubInfo.getEstablishmentTime() != null ||
                    StringUtils.hasText(clubInfo.getApprovalComment()) ||
                    (clubInfo.getStatusCode() != 0 && clubInfo.getStatusCode() != 1)
            ) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        if (club.researchClubIdByClubName(clubInfo.getClubName()) != null)
            return new Result(-20202);

        code = club.createClubInfo(clubInfo);
        if (code <= 0) return new Result(code);
        Map<String, Object> data = new HashMap<>();
        data.put("id", code);

        log.createLog("社团申请表："+code, "创建", JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(0, data);
    }

    @PostMapping("/delete")
    public Result deleteClub(@RequestHeader("Authorization") String token, @RequestBody ClubInfo clubInfo) throws Exception {
        Integer id = clubInfo.getId();
        try {
            if (id == null || id <= 0 ||
                    club.researchDetailClubInfo(id).getStatusCode() == 3
            ) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        Integer code = club.deleteClubInfo(id);

        log.createLog("社团申请表："+id, "删除", JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(code);
    }

    @PostMapping("/update")
    public Result updateClub(@RequestHeader("Authorization") String token, @RequestBody ClubInfo clubInfo) throws Exception {
        Integer code = paramsException(clubInfo);
        if (code != 0) return new Result(code);

        try {
            if (clubInfo.getTotalFund().compareTo(clubInfo.getSurplusFund()) < 0 ||
                    (clubInfo.getStatusCode() != 3 && clubInfo.getEstablishmentTime() != null) ||
                    (clubInfo.getStatusCode() == 3 && clubInfo.getEstablishmentTime() == null) ||
                    ((clubInfo.getStatusCode() == 0 || clubInfo.getStatusCode() == 1) &&
                            StringUtils.hasText(clubInfo.getApprovalComment())) ||
                    (clubInfo.getStatusCode() == 2 &&
                            !StringUtils.hasText(clubInfo.getApprovalComment()))
            ) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        if (club.researchClubIdByClubName(clubInfo.getClubName()) != null)
            return new Result(-20202);

        code = club.updateClubInfo(clubInfo);
        if (code <= 0) return new Result(code);
        Map<String, Object> data = new HashMap<>();
        data.put("id", code);

        log.createLog("社团申请表："+code, "修改", JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(0, data);
    }

    @PostMapping("/approval")
    public Result approvalClub(@RequestHeader("Authorization") String token, @RequestBody ClubInfo clubInfo) throws Exception {
        Integer id = clubInfo.getId();
        try {
            if (id == null || id <= 0 ||
                    (clubInfo.getStatusCode() == 2 &&
                            !StringUtils.hasText(clubInfo.getApprovalComment())) ||
                    clubInfo.getStatusCode() == 0 || clubInfo.getStatusCode() == 1
            ) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        ClubInfo clubInformation = club.researchDetailClubInfo(id);
        if (clubInformation == null) return new Result(-20003);
        clubInformation.setStatusCode(clubInfo.getStatusCode());
        clubInformation.setApprovalComment(clubInfo.getApprovalComment());

        Integer code = club.updateClubInfo(clubInformation);
        if (code <= 0) return new Result(code);
        Map<String, Object> data = new HashMap<>();
        data.put("id", code);

        log.createLog("社团申请表："+code, "审批", JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(0, data);
    }

    @PostMapping("/research/detail")
    public Result researchDetailClub(@RequestBody ClubInfo clubInfo) {
        Integer id = clubInfo.getId();
        try {
            if (id == null || id <= 0) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        clubInfo = club.researchDetailClubInfo(id);
        if (clubInfo == null) return new Result(-20003);

        Map<String, Object> data = new HashMap<>();
        data.put("clubInfo", clubInfo);

        return new Result(0, data);
    }

    @PostMapping("/research/not-submit")
    public Result researchNotSubmitClub(@RequestBody ClubInfo clubInfo) {
        Integer code = queryParamsException(clubInfo);
        if (code != 0) return new Result(code);

        clubInfo.setStatusCode(0);
        Map<String, Object> data = club.researchBasicClubInfo(clubInfo);

        return new Result(0, data);
    }

    @PostMapping("/research/not-approval")
    public Result researchNotApprovalClub(@RequestBody ClubInfo clubInfo) {
        Integer code = queryParamsException(clubInfo);
        if (code != 0) return new Result(code);

        clubInfo.setStatusCode(1);
        Map<String, Object> data = club.researchBasicClubInfo(clubInfo);

        return new Result(0, data);
    }

    @PostMapping("/research/been-rejected")
    public Result researchBeenRejectedClub(@RequestBody ClubInfo clubInfo) {
        Integer code = queryParamsException(clubInfo);
        if (code != 0) return new Result(code);

        clubInfo.setStatusCode(2);
        Map<String, Object> data = club.researchBasicClubInfo(clubInfo);

        return new Result(0, data);
    }

    @PostMapping("/research/been-accepted")
    public Result researchBeenAcceptedClub(@RequestBody ClubInfo clubInfo) {
        Integer code = queryParamsException(clubInfo);
        if (code != 0) return new Result(code);

        clubInfo.setStatusCode(3);
        Map<String, Object> data = club.researchBasicClubInfo(clubInfo);

        return new Result(0, data);
    }

    @PostMapping("/count")
    public Result count() {
        Map<String, Object> data = club.researchCount();

        return new Result(0, data);
    }
}
