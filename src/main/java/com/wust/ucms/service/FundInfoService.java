package com.wust.ucms.service;

import com.wust.ucms.pojo.FundInfo;

import java.util.Map;

public interface FundInfoService {
    Integer createFundInfo(FundInfo fundInfo);
    Integer deleteFundInfo(Integer fundId);
    Integer updateFundInfo(FundInfo fundInfo);
    FundInfo researchDetailFundInfo(Integer fundId);
    Map<String, Object> researchBasicFundInfo(FundInfo fundInfo);
}
