package com.wust.ucms.service;

import com.wust.ucms.pojo.UserRole;

import java.util.Map;

public interface UserRoleService {
    Integer createRole(UserRole userRole);
    Integer deleteRole(Integer roleId);
    Integer updateRole(UserRole userRole);
    Map<String, Object> researchRole(UserRole userRole);
    Integer researchRoleIdByRoleName(String roleName);
}
