package com.wust.ucms.service;

import com.wust.ucms.pojo.LoginInfo;

import java.util.Map;

public interface LoginInfoService {
    Integer createUser(LoginInfo loginInfo);
    Integer logicalDeleteUser(LoginInfo loginInfo);
    Integer logout(LoginInfo loginInfo);
    Integer update(LoginInfo loginInfo);
    Integer verify(LoginInfo loginInfo);
    LoginInfo researchDetail(LoginInfo loginInfo);
    Map<String, Object> researchBasic(LoginInfo loginInfo);
}
