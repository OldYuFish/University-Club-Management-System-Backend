package com.wust.ucms.service;

import com.wust.ucms.pojo.LoginInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface LoginInfoService {
    Long selectDateFromSQL();
    Integer setSecretKey(String secretKey, LoginInfo loginInfo);
    Integer createUser(LoginInfo loginInfo);
    Integer logicalDeleteUser(Integer loginId);
    Integer updatePassword(LoginInfo loginInfo);
    Integer updatePhone(LoginInfo loginInfo);
    Integer updateEmail(LoginInfo loginInfo);
    Integer updateRoleId(LoginInfo loginInfo);
    LoginInfo researchDetail(String email);
    LoginInfo researchDetailByPhone(String phone);
    LoginInfo researchDetailById(Integer loginId);
    LoginInfo researchDetailByUserNumber(String userNumber);
    List<LoginInfo> researchLoginInfoByRoleId(Integer roleId);
    Map<String, Object> researchBasic(LoginInfo loginInfo);
}
