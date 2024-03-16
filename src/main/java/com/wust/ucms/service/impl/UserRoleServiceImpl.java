package com.wust.ucms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wust.ucms.mapper.UserRoleMapper;
import com.wust.ucms.pojo.UserRole;
import com.wust.ucms.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    UserRoleMapper role;

    @Override
    public Integer createRole(UserRole userRole) {
        if (role.selectRoleIdByRoleName(userRole.getRoleName()) != null) return -20210;
        int flag = role.insert(userRole);
        if (flag > 0) return 0;

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public Integer deleteRole(Integer roleId) {
        int flag = role.deleteById(roleId);
        if (flag > 0) return 0;

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public Integer updateRole(UserRole userRole) {
        String roleName = userRole.getRoleName();
        if (role.selectRoleIdByRoleName(roleName) != null) return -20210;

        role.selectById(userRole.getId());

        userRole.setRoleName(roleName);
        int flag = role.updateById(userRole);
        if (flag > 0) return userRole.getId();

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public Map<String, Object> researchRole(UserRole userRole) {
        LambdaQueryWrapper<UserRole> lqw = new LambdaQueryWrapper<>();
        lqw.like(
                StringUtils.hasText(userRole.getRoleName()),
                UserRole::getRoleName,
                userRole.getRoleName()
        );

        IPage<UserRole> page = new Page<>(userRole.getPageIndex(), userRole.getPageSize());
        role.selectPage(page, lqw);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("total", page.getTotal());
        pagination.put("pageIndex", userRole.getPageIndex());
        pagination.put("pageSize", userRole.getPageSize());

        List<UserRole> roleList = page.getRecords();
        Map<String, Object> data = new HashMap<>();
        data.put("roleList", roleList);
        data.put("pagination", pagination);

        return data;
    }

    @Override
    public Integer researchRoleIdByRoleName(String roleName) {
        return role.selectRoleIdByRoleName(roleName);
    }

    @Override
    public UserRole researchUserRoleById(Integer roleId) {
        return role.selectById(roleId);
    }
}
