package com.wust.ucms.filter;

import com.wust.ucms.pojo.LoginInfo;
import com.wust.ucms.pojo.Permission;
import com.wust.ucms.pojo.RSAKeyProperties;
import com.wust.ucms.service.impl.PermissionServiceImpl;
import com.wust.ucms.utils.JWTUtil;
import com.wust.ucms.utils.RedisCache;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class JWTAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    RedisCache redisCache;

    @Autowired
    private RSAKeyProperties rsaKeyProperties;

    @Autowired
    PermissionServiceImpl permission;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        List<String> whiteList = new ArrayList<>();
        whiteList.add("/api/user/register");
        whiteList.add("/api/user/captcha");
        whiteList.add("/api/user/verify");
        whiteList.add("/api/user/cid");
        whiteList.add("/api/user/retrieve");
        whiteList.add("/api/user/qrcode");

        String token = request.getHeader("Authorization");
        String apiURI = request.getRequestURI();
        if (!StringUtils.hasText(token)) {
            if (!whiteList.contains(apiURI)) throw new RuntimeException("Missing token");
            filterChain.doFilter(request, response);
            return;
        }

        String user;
        try {
            Claims claims = JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey());
            user = claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Token is illegal");
        }

        LoginInfo loginInfo = redisCache.getCacheObject("login:" + user);

        if (Objects.isNull(loginInfo)) {
            throw new RuntimeException("The user is not login");
        }

        if (!Objects.equals(loginInfo.getToken(), token)) {
            loginInfo.setToken(token);
            redisCache.setCacheObject("login:"+user, loginInfo);
        }

        Integer roleId = loginInfo.getRoleId();
        List<Permission> permissionList = permission.researchPermissionOfRole(roleId);

        boolean flag = false;
        for (Permission p : permissionList) {
            if (Objects.equals(p.getUrl(), apiURI)) {
                flag = true;
                break;
            }
        }

        if (!flag) throw new RuntimeException("The user does not have enough permissions to access this API");

        filterChain.doFilter(request, response);
    }
}
