package com.wust.ucms.service;

import com.wust.ucms.pojo.ClubInfo;

import java.util.Map;

public interface ClubInfoService {
    Integer createClubInfo(ClubInfo clubInfo);
    Integer deleteClubInfo(Integer clubId);
    Integer updateClubInfo(ClubInfo clubInfo);
    ClubInfo researchDetailClubInfo(Integer clubId);
    Map<String, Object> researchBasicClubInfo(ClubInfo clubInfo);
    Integer researchClubIdByClubName(String clubName);
}
