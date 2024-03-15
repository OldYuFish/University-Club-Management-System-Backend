package com.wust.ucms.controller;

import com.wust.ucms.controller.utils.Result;
import com.wust.ucms.pojo.FundInfo;
import com.wust.ucms.pojo.RSAKeyProperties;
import com.wust.ucms.service.impl.FundInfoServiceImpl;
import com.wust.ucms.service.impl.LogServiceImpl;
import com.wust.ucms.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/fund")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FundController {

    @Autowired
    FundInfoServiceImpl fund;

    @Autowired
    LogServiceImpl log;

    @Autowired
    RSAKeyProperties rsaKeyProperties;

    private static Integer paramsException(FundInfo fundInfo) {
        try {
            if (!StringUtils.hasText(fundInfo.getTheme()) ||
                    !StringUtils.hasText(fundInfo.getType()) ||
                    fundInfo.getAmount() == null ||
                    fundInfo.getSurplus() == null ||
                    fundInfo.getAppropriationTime() == null ||
                    fundInfo.getClubId() == null ||
                    (fundInfo.getType().equals("竞赛奖金") &&
                            (!StringUtils.hasText(fundInfo.getCompetitionBonus().getCompetitionName()) ||
                                    !StringUtils.hasText(fundInfo.getCompetitionBonus().getType()) ||
                                    !StringUtils.hasText(fundInfo.getCompetitionBonus().getCompetitionLevel()) ||
                                    !StringUtils.hasText(fundInfo.getCompetitionBonus().getAward())))
            ) throw new Exception("缺少参数！");
        } catch (Exception e) {
            return -20001;
        }

        try {
            if (fundInfo.getTheme().length() > 24 ||
                    fundInfo.getType().length() > 8 ||
                    fundInfo.getAmount().compareTo(BigDecimal.ZERO) < 0 ||
                    fundInfo.getAmount().compareTo(new BigDecimal("99999999.99")) > 0 ||
                    fundInfo.getSurplus().compareTo(BigDecimal.ZERO) < 0 ||
                    fundInfo.getSurplus().compareTo(new BigDecimal("99999999.99")) > 0 ||
                    (fundInfo.getType().equals("竞赛奖金") &&
                            (fundInfo.getCompetitionBonus().getCompetitionName().length() > 36 ||
                                    fundInfo.getCompetitionBonus().getType().length() > 12 ||
                                    fundInfo.getCompetitionBonus().getCompetitionLevel().length() > 6 ||
                                    fundInfo.getCompetitionBonus().getAward().length() > 6))
            ) throw new Exception("参数格式错误！");
        } catch (Exception e) {
            return -20002;
        }

        return 0;
    }

    private static Integer queryParamsException(FundInfo fundInfo) {
        try {
            if (fundInfo.getPageIndex() == null ||
                    fundInfo.getPageSize() == null
            ) throw new Exception("缺少参数！");
        } catch (Exception e) {
            return -20001;
        }

        try {
            if (fundInfo.getTheme().length() > 24 ||
                    fundInfo.getType().length() > 8 ||
                    fundInfo.getClubName().length() > 24 ||
                    fundInfo.getPageIndex() <= 0 ||
                    fundInfo.getPageSize() <= 0
            ) throw new Exception("参数格式错误！");
        } catch (Exception e) {
            return -20002;
        }

        return 0;
    }

    @PostMapping("/create")
    public Result createFund(@RequestHeader("Authorization") String token, @RequestBody FundInfo fundInfo) throws Exception {
        Integer code = paramsException(fundInfo);
        if (code != 0) return new Result(code);

        try {
            if (fundInfo.getAmount().compareTo(fundInfo.getSurplus()) < 0 ||
                    (fundInfo.getStatusCode() != 0 && fundInfo.getStatusCode() != 1) ||
                    fundInfo.getCompetitionId() != null ||
                    (Objects.equals(fundInfo.getType(), "竞赛奖金") &&
                            fundInfo.getCompetitionBonus() == null)
            ) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        code = fund.createFundInfo(fundInfo);
        if (code <= 0) return new Result(code);
        Map<String, Object> data = new HashMap<>();
        data.put("id", code);

        log.createLog("经费申请表："+code, "创建", JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(0, data);
    }

    @PostMapping("/delete")
    public Result deleteFund(@RequestHeader("Authorization") String token, @RequestBody FundInfo fundInfo) throws Exception {
        Integer id = fundInfo.getId();
        try {
            if (id == null || id <= 0 ||
                    fund.researchDetailFundInfo(id).getStatusCode() == 3
            ) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        Integer code = fund.deleteFundInfo(id);

        log.createLog("经费申请表："+id, "删除", JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(code);
    }

    @PostMapping("/update")
    public Result updateFund(@RequestHeader("Authorization") String token, @RequestBody FundInfo fundInfo) throws Exception {
        Integer code = paramsException(fundInfo);
        if (code != 0) return new Result(code);

        try {
            if (fundInfo.getAmount().compareTo(fundInfo.getSurplus()) < 0 ||
                    ((fundInfo.getStatusCode() == 0 || fundInfo.getStatusCode() == 1) &&
                            StringUtils.hasText(fundInfo.getApprovalComment())) ||
                    (fundInfo.getStatusCode() == 2 &&
                            !StringUtils.hasText(fundInfo.getApprovalComment())) ||
                    (Objects.equals(fundInfo.getType(), "竞赛奖金") &&
                            fundInfo.getCompetitionBonus() == null)
            ) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        code = fund.updateFundInfo(fundInfo);
        if (code <= 0) return new Result(code);
        Map<String, Object> data = new HashMap<>();
        data.put("id", code);

        log.createLog("经费申请表："+code, "修改", JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(0, data);
    }

    @PostMapping("/approval")
    public Result approvalFund(@RequestHeader("Authorization") String token, @RequestBody FundInfo fundInfo) throws Exception {
        Integer id = fundInfo.getId();
        try {
            if (id == null || id <= 0 ||
                    (fundInfo.getStatusCode() == 2 &&
                            !StringUtils.hasText(fundInfo.getApprovalComment())) ||
                    fundInfo.getStatusCode() == 0 || fundInfo.getStatusCode() == 1
            ) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        FundInfo fundInformation = fund.researchDetailFundInfo(fundInfo.getId());
        if (fundInformation == null) return new Result(-20003);
        fundInformation.setStatusCode(fundInfo.getStatusCode());
        fundInformation.setApprovalComment(fundInfo.getApprovalComment());

        Integer code = fund.updateFundInfo(fundInformation);
        if (code <= 0) return new Result(code);
        Map<String, Object> data = new HashMap<>();
        data.put("id", code);

        log.createLog("经费申请表："+code, "审批", JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(0, data);
    }

    @PostMapping("/research/detail")
    public Result researchDetailFund(@RequestBody FundInfo fundInfo) {
        Integer id = fundInfo.getId();
        try {
            if (id == null || id <= 0) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        fundInfo = fund.researchDetailFundInfo(id);
        if (fundInfo == null) return new Result(-20003);

        Map<String, Object> data = new HashMap<>();
        data.put("fundInfo", fundInfo);

        return new Result(0, data);
    }

    @PostMapping("/research/not-submit")
    public Result researchNotSubmitFund(@RequestBody FundInfo fundInfo) {
        Integer code = queryParamsException(fundInfo);
        if (code != 0) return new Result(code);

        fundInfo.setStatusCode(0);
        Map<String, Object> data = fund.researchBasicFundInfo(fundInfo);

        return new Result(0, data);
    }

    @PostMapping("/research/not-approval")
    public Result researchNotApprovalFund(@RequestBody FundInfo fundInfo) {
        Integer code = queryParamsException(fundInfo);
        if (code != 0) return new Result(code);

        fundInfo.setStatusCode(1);
        Map<String, Object> data = fund.researchBasicFundInfo(fundInfo);

        return new Result(0, data);
    }

    @PostMapping("/research/been-rejected")
    public Result researchBeenRejectedFund(@RequestBody FundInfo fundInfo) {
        Integer code = queryParamsException(fundInfo);
        if (code != 0) return new Result(code);

        fundInfo.setStatusCode(2);
        Map<String, Object> data = fund.researchBasicFundInfo(fundInfo);

        return new Result(0, data);
    }

    @PostMapping("/research/been-accepted")
    public Result researchBeenAcceptedFund(@RequestBody FundInfo fundInfo) {
        Integer code = queryParamsException(fundInfo);
        if (code != 0) return new Result(code);

        fundInfo.setStatusCode(3);
        Map<String, Object> data = fund.researchBasicFundInfo(fundInfo);

        return new Result(0, data);
    }
}
