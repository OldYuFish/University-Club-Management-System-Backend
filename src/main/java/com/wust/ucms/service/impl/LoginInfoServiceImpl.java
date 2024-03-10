package com.wust.ucms.service.impl;

import com.wust.ucms.mapper.LoginInfoMapper;
import com.wust.ucms.pojo.LoginInfo;
import com.wust.ucms.service.LoginInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LoginInfoServiceImpl implements LoginInfoService {

    @Autowired
    LoginInfoMapper login;

    @Override
    public Integer createUser(LoginInfo loginInfo) {
        return null;
    }

    @Override
    public Integer logicalDeleteUser(LoginInfo loginInfo) {
        return null;
    }

    @Override
    public Integer logout(LoginInfo loginInfo) {
        return null;
    }

    @Override
    public Integer update(LoginInfo loginInfo) {
        return null;
    }

    @Override
    public Integer verify(LoginInfo loginInfo) {
        return null;
    }

    @Override
    public LoginInfo researchDetail(LoginInfo loginInfo) {
        return null;
    }

    @Override
    public Map<String, Object> researchBasic(LoginInfo loginInfo) {
        return null;
    }
}
