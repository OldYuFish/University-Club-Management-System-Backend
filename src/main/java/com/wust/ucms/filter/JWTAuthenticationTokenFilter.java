package com.wust.ucms.filter;

import com.wust.ucms.pojo.LoginInfo;
import com.wust.ucms.pojo.RSAKeyProperties;
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
import java.util.Objects;

@Component
public class JWTAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    RedisCache redisCache;

    @Autowired
    private RSAKeyProperties rsaKeyProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String userId;
        try {
            Claims claims = JWTUtil.parseJWT(token, rsaKeyProperties.getPublicKey());
            userId = claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Token is illegal");
        }

        LoginInfo loginInfo = redisCache.getCacheObject("login:" + userId);

        if (Objects.isNull(loginInfo)) {
            throw new RuntimeException("The user is not login");
        }

        if (!Objects.equals(loginInfo.getToken(), token)) {
            loginInfo.setToken(token);
            redisCache.setCacheObject("login:" + userId, loginInfo);
        }

        filterChain.doFilter(request, response);
    }
}
