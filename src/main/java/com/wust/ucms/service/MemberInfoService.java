package com.wust.ucms.service;

import com.wust.ucms.pojo.MemberInfo;

import java.util.List;

public interface MemberInfoService {
    Integer createMemberInfo(MemberInfo memberInfo);
    Integer deleteMemberInfo(Integer memberId);
    Integer updateMemberInfo(MemberInfo memberInfo);
    List<MemberInfo> researchMemberInfo(Integer clubId);
}
