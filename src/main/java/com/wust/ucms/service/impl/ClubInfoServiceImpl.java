package com.wust.ucms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wust.ucms.mapper.ClubInfoMapper;
import com.wust.ucms.mapper.LoginInfoMapper;
import com.wust.ucms.pojo.ClubInfo;
import com.wust.ucms.pojo.LoginInfo;
import com.wust.ucms.service.ClubInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ClubInfoServiceImpl implements ClubInfoService {

    @Autowired
    ClubInfoMapper club;

    @Autowired
    LoginInfoMapper login;

    @Override
    public Integer createClubInfo(ClubInfo clubInfo) {
        clubInfo.setLoginId(login.selectLoginIdByStudentNumber(clubInfo.getStudentNumber()));
        if (club.selectClubIdByLoginId(clubInfo.getLoginId()) != null)
            return -20209;
        int flag = club.insert(clubInfo);
        if (flag > 0) return clubInfo.getId();

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public Integer deleteClubInfo(Integer clubId) {
        int flag = club.deleteById(clubId);
        if (flag > 0) return 0;

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public Integer updateClubInfo(ClubInfo clubInfo) {
        String clubName = clubInfo.getClubName();
        Integer membersNumber = clubInfo.getMembersNumber();
        String type = clubInfo.getType();
        String clubLevel = clubInfo.getClubLevel();
        String department = clubInfo.getDepartment();
        BigDecimal totalFund = clubInfo.getTotalFund();
        BigDecimal surplusFund = clubInfo.getSurplusFund();
        String description = clubInfo.getDescription();
        Date applicationTime = clubInfo.getApplicationTime();
        Date establishmentTime = clubInfo.getEstablishmentTime();
        Integer statusCode = clubInfo.getStatusCode();
        String approvalComment = clubInfo.getApprovalComment();
        Integer loginId = clubInfo.getLoginId();

        clubInfo = club.selectById(clubInfo.getId());

        clubInfo.setClubName(clubName);
        clubInfo.setMembersNumber(membersNumber);
        clubInfo.setType(type);
        clubInfo.setClubLevel(clubLevel);
        clubInfo.setDepartment(department);
        clubInfo.setTotalFund(totalFund);
        clubInfo.setSurplusFund(surplusFund);
        clubInfo.setDescription(description);
        clubInfo.setApplicationTime(applicationTime);
        clubInfo.setEstablishmentTime(establishmentTime);
        clubInfo.setStatusCode(statusCode);
        clubInfo.setApprovalComment(approvalComment);
        clubInfo.setLoginId(loginId);

        int flag = club.updateById(clubInfo);
        if (flag > 0) return 0;

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public ClubInfo researchDetailClubInfo(Integer clubId) {
        ClubInfo clubInfo = club.selectById(clubId);
        LoginInfo loginInfo = login.selectById(clubInfo.getLoginId());
        clubInfo.setRealName(loginInfo.getRealName());
        clubInfo.setStudentNumber(loginInfo.getStudentNumber());
        clubInfo.setEmail(loginInfo.getEmail());
        return clubInfo;
    }

    @Override
    public Map<String, Object> researchBasicClubInfo(ClubInfo clubInfo) {
        LambdaQueryWrapper<ClubInfo> lqw = new LambdaQueryWrapper<>();
        lqw.like(
                clubInfo.getClubName() != null && !clubInfo.getClubName().isEmpty(),
                ClubInfo::getClubName,
                clubInfo.getClubName()
        ).eq(
                clubInfo.getType() != null && !clubInfo.getType().isEmpty(),
                ClubInfo::getType,
                clubInfo.getType()
        ).eq(
                clubInfo.getClubLevel() != null && !clubInfo.getClubLevel().isEmpty(),
                ClubInfo::getClubLevel,
                clubInfo.getClubLevel()
        ).like(
                clubInfo.getDepartment() != null && !clubInfo.getDepartment().isEmpty(),
                ClubInfo::getDepartment,
                clubInfo.getDepartment()
        ).eq(
                clubInfo.getStatusCode() != null,
                ClubInfo::getStatusCode,
                clubInfo.getStatusCode()
        );

        IPage<ClubInfo> page = new Page<>(clubInfo.getPageIndex(), clubInfo.getPageSize());
        club.selectPage(page, lqw);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("total", page.getTotal());
        pagination.put("pageIndex", clubInfo.getPageIndex());
        pagination.put("pageSize", clubInfo.getPageSize());

        List<Map<String, Object>> clubList = new ArrayList<>();
        for (ClubInfo c: page.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("clubName", c.getClubName());
            map.put("type", c.getType());
            map.put("clubLevel", c.getClubLevel());
            map.put("department", c.getDepartment() == null || c.getDepartment().isEmpty() ? "--" : c.getDepartment());
            map.put("statusCode", c.getStatusCode());
            map.put("realName", login.selectById(c.getLoginId()).getRealName());
            clubList.add(map);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("clubList", clubList);
        data.put("pagination", pagination);

        return data;
    }

    @Override
    public Integer researchClubIdByClubName(String clubName) {
        return club.selectClubIdByClubName(clubName);
    }
}
