package com.wust.ucms.service.impl;

import com.wust.ucms.mapper.LoginInfoMapper;
import com.wust.ucms.pojo.LoginInfo;
import com.wust.ucms.service.LoginInfoService;
import com.wust.ucms.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

@Service
public class LoginInfoServiceImpl implements LoginInfoService {

    @Autowired
    LoginInfoMapper login;

    @Autowired
    RedisCache redis;

    @Override
    public Integer createUser(LoginInfo loginInfo) {
        String studentNumber = loginInfo.getStudentNumber();
        String teacherNumber = loginInfo.getTeacherNumber();

        if (login.selectLoginIdByPhone(loginInfo.getPhone()) != null ||
                login.selectLoginIdByEmail(loginInfo.getEmail()) != null ||
                ((studentNumber != null && !studentNumber.isEmpty()) &&
                        login.selectLoginIdByStudentNumber(studentNumber) != null) ||
                ((teacherNumber != null && !teacherNumber.isEmpty()) &&
                        login.selectLoginIdByTeacherNumber(teacherNumber) != null)
        ) return -20201;

        List<LoginInfo> deleteLoginInfo = login.selectDeleteLoginInfo();
        LoginInfo deleteInfo = null;
        if (studentNumber != null && !studentNumber.isEmpty()) {
            for (LoginInfo l : deleteLoginInfo) {
                if (studentNumber.replace("0", "O").replace("1", "I").equals(l.getStudentNumber())) {
                    deleteInfo = l;
                    break;
                }
            }
        } else {
            assert teacherNumber != null;
            for (LoginInfo l : deleteLoginInfo) {
                if (teacherNumber.replace("0", "O").replace("1", "I").equals(l.getTeacherNumber())) {
                    deleteInfo = l;
                    break;
                }
            }
        }

        if (deleteInfo != null) {
            deleteInfo.setRealName(loginInfo.getRealName());
            deleteInfo.setPassword(loginInfo.getPassword());
            if (studentNumber != null && !studentNumber.isEmpty())
                deleteInfo.setStudentNumber(studentNumber);
            else  deleteInfo.setTeacherNumber(teacherNumber);
            deleteInfo.setPhone(loginInfo.getPhone());
            deleteInfo.setEmail(loginInfo.getEmail());
            deleteInfo.setIsDelete(0);
            deleteInfo.setRoleId(1);

            int flag = login.updateById(deleteInfo);
            if (flag > 0) return deleteInfo.getId();

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return -20003;
        }

        int flag = login.insert(loginInfo);
        if (flag > 0) return loginInfo.getId();

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public Integer logicalDeleteUser(Integer loginId) {
        LoginInfo loginInfo = login.selectById(loginId);
        if (loginInfo == null || loginInfo.getIsDelete() == 1) return -20203;

        loginInfo.setPhone(loginInfo.getPhone().replace("1", "I"));
        loginInfo.setEmail(loginInfo.getEmail().replace("@", "A"));
        if (loginInfo.getStudentNumber() != null && !loginInfo.getStudentNumber().isEmpty())
            loginInfo.setStudentNumber(loginInfo.getStudentNumber().replace("0", "O").replace("1", "I"));
        else loginInfo.setTeacherNumber(loginInfo.getTeacherNumber().replace("0", "O").replace("1", "I"));
        loginInfo.setIsDelete(1);

        int flag = login.updateById(loginInfo);
        if (flag > 0) return 0;

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public Integer logout(Integer loginId) {
        redis.deleteObject("login:" + loginId.toString());
        return 0;
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
