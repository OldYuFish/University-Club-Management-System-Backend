package com.wust.ucms.controller;

import com.wust.ucms.controller.utils.Result;
import com.wust.ucms.pojo.LoginInfo;
import com.wust.ucms.pojo.Permission;
import com.wust.ucms.pojo.RSAKeyProperties;
import com.wust.ucms.pojo.UserRole;
import com.wust.ucms.service.impl.*;
import com.wust.ucms.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/role")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoleController {

    @Autowired
    UserRoleServiceImpl role;

    @Autowired
    PermissionServiceImpl permission;

    @Autowired
    RolePermissionServiceImpl rolePer;

    @Autowired
    LoginInfoServiceImpl login;

    @Autowired
    LogServiceImpl log;

    @Autowired
    RSAKeyProperties rsaKeyProperties;

    @PostMapping("/create")
    public Result createRole(@RequestHeader("Authorization") String token, @RequestBody UserRole userRole) throws Exception {
        if (!StringUtils.hasText(userRole.getRoleName())) return new Result(-20001);
        if (userRole.getRoleName().length() > 12) return new Result(-20002);
        Integer code = role.createRole(userRole);
        if (code <= 0) return new Result(code);
        Map<String, Object> data = new HashMap<>();
        data.put("id", code);

        log.createLog("用户组："+code, "创建", JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(0, data);
    }

    @PostMapping("/delete/role")
    public Result deleteRole(@RequestHeader("Authorization") String token, @RequestBody UserRole userRole) throws Exception {
        Integer id = userRole.getId();
        try {
            if (id == null || id <= 0) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        Integer code = role.deleteRole(id);

        log.createLog("用户组："+id, "删除", JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(code);
    }

    @PostMapping("/delete/user")
    public Result deleteUserInRole(@RequestHeader("Authorization") String token, @RequestBody UserRole userRole) throws Exception {
        Integer id = userRole.getId();
        try {
            if (id == null || id <= 0) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        List<LoginInfo> loginList = login.researchLoginInfoByRoleId(id);
        for (LoginInfo l : loginList)
            login.logicalDeleteUser(l.getId());

        log.createLog("用户组："+id, "删除组内所有用户", JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(0);
    }

    @PostMapping("/update/role")
    public Result updateRole(@RequestHeader("Authorization") String token, @RequestBody UserRole userRole) throws Exception {
        if (!StringUtils.hasText(userRole.getRoleName()) ||
                userRole.getId() == null) return new Result(-20001);
        if (userRole.getRoleName().length() > 12) return new Result(-20002);

        Integer code = role.updateRole(userRole);

        log.createLog("用户组："+userRole.getId(), "修改名称", JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(code);
    }

    @PostMapping("/update/user")
    public Result updateUserRole(@RequestHeader("Authorization") String token, @RequestBody UserRole userRole) throws Exception {
        Integer id = userRole.getId();
        String roleName = userRole.getRoleName();
        if (!StringUtils.hasText(roleName)) return new Result(-20001);
        try {
            if (id == null || id <= 0) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }
        if (role.researchRoleIdByRoleName(roleName) == null) return new Result(-20209);

        List<LoginInfo> loginList = login.researchLoginInfoByRoleId(id);
        for (LoginInfo l : loginList) {
            l.setRoleId(role.researchRoleIdByRoleName(roleName));
            login.updateRoleId(l);
        }

        log.createLog("用户组："+id, "组内所有用户转移至用户组："+role.researchRoleIdByRoleName(roleName), JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(0);
    }

    @PostMapping("/update/permission")
    public Result updateRolePermission(@RequestHeader("Authorization") String token, @RequestBody UserRole userRole) throws Exception {
        List<Permission> permissionList = userRole.getPermissionList();
        Integer id = userRole.getId();
        if (permissionList == null) return new Result(-20001);

        try {
            if (id == null || id <= 0) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        Integer code = rolePer.deleteRolePermissionByRoleId(id);
        if (code <= 0) return new Result(code);

        code = rolePer.create(id, permissionList);

        log.createLog("用户组："+id, "修改权限", JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey()).getSubject());

        return new Result(code);
    }

    @PostMapping("/research/permission")
    public Result researchPermissionOfRole(@RequestBody UserRole userRole) {
        Integer id = userRole.getId();
        try {
            if (id == null || id <= 0) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        List<Permission> permissionList = permission.researchPermissionOfRole(id);
        Map<String, Object> data = new HashMap<>();
        data.put("permissionList", permissionList);

        return new Result(0, data);
    }

    @PostMapping("/research/role")
    public Result researchRole(@RequestBody UserRole userRole) {
        try {
            if (userRole.getPageIndex() == null ||
                    userRole.getPageSize() == null
            ) throw new Exception("缺少参数！");
        } catch (Exception e) {
            return new Result(-20001);
        }

        Map<String, Object> data = role.researchRole(userRole);

        return new Result(0, data);
    }
}
