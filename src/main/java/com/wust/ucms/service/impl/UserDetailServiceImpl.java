package com.wust.ucms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wust.ucms.mapper.LoginInfoMapper;
import com.wust.ucms.mapper.UserRoleMapper;
import com.wust.ucms.pojo.LoginInfo;
import com.wust.ucms.pojo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isNumeric;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    LoginInfoMapper user;

    @Autowired
    UserRoleMapper role;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<LoginInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (!isNumeric(username)) {
            throw new RuntimeException("The user id is not a number.");
        }

        lambdaQueryWrapper.eq(LoginInfo::getId, Integer.parseInt(username));

        LoginInfo userInfo = user.selectOne(lambdaQueryWrapper);

        if (Objects.isNull(userInfo)) {
            throw new RuntimeException("The user is not registered.");
        }

        String roleName = role.selectById(userInfo.getRoleId()).getRoleName();

        return new LoginUser(userInfo, roleName);
    }
}
