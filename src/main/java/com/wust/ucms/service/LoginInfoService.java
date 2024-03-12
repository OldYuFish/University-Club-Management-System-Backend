package com.wust.ucms.service;

import com.wust.ucms.pojo.LoginInfo;

import java.util.Map;

public interface LoginInfoService {
    Integer createUser(LoginInfo loginInfo);
    Integer logicalDeleteUser(Integer loginId);
    Integer logout(Integer loginId);
    Integer updatePassword(LoginInfo loginInfo);
    Integer updatePhone(LoginInfo loginInfo);
    Integer updateEmail(LoginInfo loginInfo);
    Integer updateRoleId(LoginInfo loginInfo);
    LoginInfo researchDetail(String email);
    Map<String, Object> researchBasic(LoginInfo loginInfo);
}
