package com.wust.ucms.service.impl;

import com.wust.ucms.mapper.MemberInfoMapper;
import com.wust.ucms.pojo.MemberInfo;
import com.wust.ucms.service.MemberInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Service
public class MemberInfoServiceImpl implements MemberInfoService {

    @Autowired
    MemberInfoMapper member;

    @Override
    public Integer createMemberInfo(MemberInfo memberInfo) {
        int flag = member.insert(memberInfo);
        if (flag > 0) return memberInfo.getId();

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public Integer deleteMemberInfo(Integer memberId) {
        int flag = member.deleteById(memberId);
        if (flag > 0) return 0;

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public Integer updateMemberInfo(MemberInfo memberInfo) {
        String memberName = memberInfo.getMemberName();
        String department = memberInfo.getDepartment();
        String job = memberInfo.getJob();
        String studentNumber = memberInfo.getStudentNumber();
        String email = memberInfo.getEmail();
        String honor = memberInfo.getHonor();
        String description = memberInfo.getDescription();

        memberInfo = member.selectById(memberInfo.getId());

        memberInfo.setMemberName(memberName);
        memberInfo.setDepartment(department);
        memberInfo.setJob(job);
        memberInfo.setStudentNumber(studentNumber);
        memberInfo.setEmail(email);
        memberInfo.setHonor(honor);
        memberInfo.setDescription(description);

        int flag = member.updateById(memberInfo);
        if (flag > 0) return memberInfo.getId();

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public List<MemberInfo> researchMemberInfo(Integer clubId) {
        return member.selectMemberInfoByClubId(clubId);
    }
}
