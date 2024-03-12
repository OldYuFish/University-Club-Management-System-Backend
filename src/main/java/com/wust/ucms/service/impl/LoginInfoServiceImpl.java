package com.wust.ucms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wust.ucms.mapper.LoginInfoMapper;
import com.wust.ucms.mapper.UserRoleMapper;
import com.wust.ucms.pojo.LoginInfo;
import com.wust.ucms.service.LoginInfoService;
import com.wust.ucms.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoginInfoServiceImpl implements LoginInfoService {

    @Autowired
    LoginInfoMapper login;

    @Autowired
    UserRoleMapper role;

    @Autowired
    RedisCache redis;

    @Override
    public Long selectDateFromSQL() {
        return login.selectDateFromSQL().getTime();
    }

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
    public Integer updatePassword(LoginInfo loginInfo) {
        Integer loginId = loginInfo.getId();
        String password = loginInfo.getPassword();

        LoginInfo loginInformation = login.selectById(loginId);
        loginInformation.setPassword(password);
        int flag = login.updateById(loginInformation);
        if (flag > 0) return loginId;

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public Integer updatePhone(LoginInfo loginInfo) {
        Integer loginId = loginInfo.getId();
        String phone = loginInfo.getPhone();

        if (login.selectLoginIdByPhone(phone) != null) return -20201;

        LoginInfo loginInformation = login.selectById(loginId);
        loginInformation.setPhone(phone);
        int flag = login.updateById(loginInformation);
        if (flag > 0) return loginId;

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public Integer updateEmail(LoginInfo loginInfo) {
        Integer loginId = loginInfo.getId();
        String email = loginInfo.getEmail();

        if (login.selectLoginIdByEmail(email) != null) return -20201;

        LoginInfo loginInformation = login.selectById(loginId);
        loginInformation.setEmail(email);
        int flag = login.updateById(loginInformation);
        if (flag > 0) return loginId;

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public Integer updateRoleId(LoginInfo loginInfo) {
        Integer loginId = loginInfo.getId();
        Integer roleId = loginInfo.getRoleId();

        LoginInfo loginInformation = login.selectById(loginId);
        loginInformation.setRoleId(roleId);
        int flag = login.updateById(loginInformation);
        if (flag > 0) return loginId;

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public LoginInfo researchDetail(String email) {
        Integer loginId = login.selectLoginIdByEmail(email);

        return login.selectById(loginId);
    }

    @Override
    public Map<String, Object> researchBasic(LoginInfo loginInfo) {
        LambdaQueryWrapper<LoginInfo> lqw = new LambdaQueryWrapper<>();
        lqw.like(
                StringUtils.hasText(loginInfo.getRealName()),
                LoginInfo::getRealName,
                loginInfo.getRealName()
        ).like(
                StringUtils.hasText(loginInfo.getStudentNumber()),
                LoginInfo::getStudentNumber,
                loginInfo.getStudentNumber()
        ).like(
                StringUtils.hasText(loginInfo.getTeacherNumber()),
                LoginInfo::getTeacherNumber,
                loginInfo.getTeacherNumber()
        ).like(
                StringUtils.hasText(loginInfo.getPhone()),
                LoginInfo::getPhone,
                loginInfo.getPhone()
        ).like(
                StringUtils.hasText(loginInfo.getEmail()),
                LoginInfo::getEmail,
                loginInfo.getEmail()
        ).eq(
                loginInfo.getIsDelete() != null,
                LoginInfo::getIsDelete,
                loginInfo.getIsDelete()
        ).eq(
                role.selectRoleIdByRoleName(loginInfo.getRoleName()) != null,
                LoginInfo::getRoleId,
                role.selectRoleIdByRoleName(loginInfo.getRoleName())
        );

        IPage<LoginInfo> page = new Page<>(loginInfo.getPageIndex(), loginInfo.getPageSize());
        login.selectPage(page, lqw);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("total", page.getTotal());
        pagination.put("pageIndex", loginInfo.getPageIndex());
        pagination.put("pageSize", loginInfo.getPageSize());

        List<LoginInfo> loginList = page.getRecords();
        Map<String, Object> data = new HashMap<>();
        data.put("loginList", loginList);
        data.put("pagination", pagination);

        return data;
    }
}
