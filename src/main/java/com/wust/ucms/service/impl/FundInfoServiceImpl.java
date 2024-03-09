package com.wust.ucms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wust.ucms.mapper.ActivityInfoMapper;
import com.wust.ucms.mapper.ClubInfoMapper;
import com.wust.ucms.mapper.CompetitionBonusMapper;
import com.wust.ucms.mapper.FundInfoMapper;
import com.wust.ucms.pojo.CompetitionBonus;
import com.wust.ucms.pojo.FundInfo;
import com.wust.ucms.service.FundInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.*;

@Service
public class FundInfoServiceImpl implements FundInfoService {

    @Autowired
    FundInfoMapper fund;

    @Autowired
    CompetitionBonusMapper competition;

    @Autowired
    ClubInfoMapper club;

    @Autowired
    ActivityInfoMapper activity;

    @Override
    public Integer createFundInfo(FundInfo fundInfo) {
        if (Objects.equals(fundInfo.getType(), "竞赛奖金")) {
            CompetitionBonus competitionBonus = fundInfo.getCompetitionBonus();
            int flag = competition.insert(competitionBonus);
            if (flag > 0) {
                fundInfo.setCompetitionId(competitionBonus.getId());
                flag = fund.insert(fundInfo);
                while (flag <= 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    flag = fund.insert(fundInfo);
                }

                return fundInfo.getId();
            }
        } else {
            int flag = fund.insert(fundInfo);
            if (flag > 0) return fundInfo.getId();
        }

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public Integer deleteFundInfo(Integer fundId) {
        int flag;
        if (Objects.equals(fund.selectById(fundId).getType(), "竞赛奖金")) {
            flag = competition.deleteById(fund.selectById(fundId).getCompetitionId());
            if (flag > 0) {
                flag = fund.deleteById(fundId);
                while (flag <= 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    flag = fund.deleteById(fundId);
                }
                return 0;
            }
        } else {
            flag = fund.deleteById(fundId);
            if (flag > 0) return 0;
        }

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public Integer updateFundInfo(FundInfo fundInfo) {
        String theme = fundInfo.getTheme();
        String type = fundInfo.getType();
        BigDecimal amount = fundInfo.getAmount();
        BigDecimal surplus = fundInfo.getSurplus();
        Date appropriationTime = fundInfo.getAppropriationTime();
        Integer statusCode = fundInfo.getStatusCode();
        String approvalComment = fundInfo.getApprovalComment();
        Integer clubId = fundInfo.getClubId();
        Integer competitionId = fundInfo.getCompetitionId();
        Integer activityId = fundInfo.getActivityId();
        CompetitionBonus competitionBonus = fundInfo.getCompetitionBonus();

        fundInfo = fund.selectById(fundInfo.getId());
        if (!Objects.equals(fundInfo.getType(), "竞赛奖金") && Objects.equals(type, "竞赛奖金")) {
            int flag = competition.insert(competitionBonus);
            while (flag <= 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                flag = competition.insert(competitionBonus);
            }
            fundInfo.setCompetitionId(competitionBonus.getId());
        } else if (Objects.equals(fundInfo.getType(), "竞赛奖金") && !Objects.equals(type, "竞赛奖金")) {
            int flag = competition.deleteById(fundInfo.getId());
            while (flag <= 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                flag = competition.deleteById(fundInfo.getId());
            }
            fundInfo.setCompetitionId(null);
        } else if (Objects.equals(fundInfo.getType(), "竞赛奖金") && Objects.equals(type, "竞赛奖金")) {
            competitionBonus.setId(fundInfo.getCompetitionId());
            int flag = competition.updateById(competitionBonus);
            while (flag <= 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                flag = competition.updateById(competitionBonus);
            }
        }

        fundInfo.setTheme(theme);
        fundInfo.setType(type);
        fundInfo.setAmount(amount);
        fundInfo.setSurplus(surplus);
        fundInfo.setAppropriationTime(appropriationTime);
        fundInfo.setStatusCode(statusCode);
        fundInfo.setApprovalComment(approvalComment);
        fundInfo.setClubId(clubId);
        fundInfo.setActivityId(activityId);

        int flag = fund.updateById(fundInfo);
        if (flag > 0) return fundInfo.getId();

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public FundInfo researchDetailFundInfo(Integer fundId) {
        FundInfo fundInfo = fund.selectById(fundId);
        fundInfo.setClubName(club.selectById(fundInfo.getClubId()).getClubName());
        if (Objects.equals(fundInfo.getType(), "活动经费"))
            fundInfo.setTitle(activity.selectById(fundInfo.getActivityId()).getTitle());
        if (Objects.equals(fundInfo.getType(), "竞赛奖金")) {
            CompetitionBonus competitionBonus = competition.selectById(fundInfo.getCompetitionId());
            fundInfo.setCompetitionBonus(competitionBonus);
        }
        return fundInfo;
    }

    @Override
    public Map<String, Object> researchBasicFundInfo(FundInfo fundInfo) {
        LambdaQueryWrapper<FundInfo> lqw = new LambdaQueryWrapper<>();
        lqw.like(
                fundInfo.getTheme() != null && !fundInfo.getTheme().isEmpty(),
                FundInfo::getTheme,
                fundInfo.getTheme()
        ).eq(
                fundInfo.getType() != null && !fundInfo.getType().isEmpty(),
                FundInfo::getType,
                fundInfo.getType()
        ).eq(
                fundInfo.getStatusCode() != null,
                FundInfo::getStatusCode,
                fundInfo.getStatusCode()
        ).eq(
                club.selectClubIdByClubName(fundInfo.getClubName()) != null,
                FundInfo::getClubId,
                club.selectClubIdByClubName(fundInfo.getClubName())
        );

        IPage<FundInfo> page = new Page<>(fundInfo.getPageIndex(), fundInfo.getPageSize());
        fund.selectPage(page, lqw);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("total", page.getTotal());
        pagination.put("pageIndex", fundInfo.getPageIndex());
        pagination.put("pageSize", fundInfo.getPageSize());

        List<Map<String, Object>> fundList = new ArrayList<>();
        for (FundInfo f : page.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("theme", f.getTheme());
            map.put("type", f.getType());
            map.put("amount", f.getAmount());
            map.put("surplus", f.getSurplus());
            map.put("statusCode", f.getStatusCode());
            map.put("clubName", club.selectById(f.getClubId()).getClubName());
            fundList.add(map);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("fundList", fundList);
        data.put("pagination", pagination);

        return data;
    }
}
