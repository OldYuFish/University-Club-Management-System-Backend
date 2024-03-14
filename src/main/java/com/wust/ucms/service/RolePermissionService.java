package com.wust.ucms.service;

import com.wust.ucms.pojo.Permission;

import java.util.List;

public interface RolePermissionService {
    Integer create(Integer roleId, List<Permission> permissionId);
    Integer deleteRolePermissionByRoleId(Integer roleId);
}
