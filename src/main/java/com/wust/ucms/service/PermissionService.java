package com.wust.ucms.service;

import com.wust.ucms.pojo.Permission;

import java.util.List;

public interface PermissionService {
    List<Permission> researchPermissionOfRole(Integer roleId);
}
