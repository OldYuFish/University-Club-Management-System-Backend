package com.wust.ucms.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.UUID;

public class JWTUtil {

    public static final Long JWT_TTL = 24*60*60*1000L;

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String createJWT(String subject, PrivateKey privateKey) throws Exception {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID(), privateKey);
        return builder.compact();
    }

    public static String createJWT(String subject, Long ttlMillis, PrivateKey privateKey) throws Exception {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID(), privateKey);
        return builder.compact();
    }

    public static String createJWT(String id, String subject, Long ttlMillis, PrivateKey privateKey) throws Exception {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, id, privateKey);
        return builder.compact();
    }

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid, PrivateKey privateKey) throws Exception {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if(ttlMillis == null) {
            ttlMillis = JWTUtil.JWT_TTL;
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        return Jwts.builder()
                .setId(uuid)
                .setSubject(subject)
                .setIssuer("wust")
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, privateKey)
                .setExpiration(expDate);
    }

    public static Claims parseJWT(String jwt, PublicKey publicKey) throws Exception {
        return Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(jwt)
                .getBody();
    }
}
