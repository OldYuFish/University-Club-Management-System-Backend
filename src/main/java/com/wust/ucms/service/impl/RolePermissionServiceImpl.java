package com.wust.ucms.service.impl;

import com.wust.ucms.mapper.RolePermissionMapper;
import com.wust.ucms.pojo.Permission;
import com.wust.ucms.pojo.RolePermission;
import com.wust.ucms.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    @Autowired
    RolePermissionMapper rolePer;

    @Override
    public Integer create(Integer roleId, List<Permission> permissionId) {
        for (Permission p : permissionId) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(p.getId());
            rolePer.insert(rolePermission);
        }

        return 0;
    }

    @Override
    public Integer deleteRolePermissionByRoleId(Integer roleId) {
        int flag = rolePer.deleteRolePermissionByRoleId(roleId);
        if (flag > 0) return 0;

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }
}
