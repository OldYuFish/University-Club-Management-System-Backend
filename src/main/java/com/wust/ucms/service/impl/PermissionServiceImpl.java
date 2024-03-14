package com.wust.ucms.service.impl;

import com.wust.ucms.mapper.PermissionMapper;
import com.wust.ucms.mapper.RolePermissionMapper;
import com.wust.ucms.pojo.Permission;
import com.wust.ucms.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    PermissionMapper permission;

    @Autowired
    RolePermissionMapper rolePermission;

    @Override
    public List<Permission> researchPermissionOfRole(Integer roleId) {
        List<Integer> permissionIdList = rolePermission.selectPermissionByRoleId(roleId);
        List<Permission> permissionList = new ArrayList<>();
        for (Integer permissionId : permissionIdList)
            permissionList.add(permission.selectById(permissionId));

        return permissionList;
    }
}
