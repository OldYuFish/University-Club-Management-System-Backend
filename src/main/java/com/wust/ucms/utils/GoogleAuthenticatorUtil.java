package com.wust.ucms.utils;

import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.text.StringSubstitutor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class GoogleAuthenticatorUtil {

    private static final int TIME_OFFSET = 1;

    public static String createSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return Base64.encodeBase64String(bytes).toLowerCase();
    }

    public static String generateTOTP(String secretKey, long time) {
        byte[] bytes = Base64.decodeBase64(secretKey.toUpperCase());
        String hexKey = Hex.encodeHexString(bytes);
        String hexTime = Long.toHexString(time);
        return TOTPUtil.generateTOTP(hexKey, hexTime, "6");
    }

    @SneakyThrows
    public static String createKeyUri(String secret, String account, String issuer) {
        String qrCodeStr = "otpauth://totp/${issuer}:${account}?secret=${secret}&issuer=${issuer}";
        ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
        mapBuilder.put("account", URLEncoder.encode(account, StandardCharsets.UTF_8).replace("+", "%20"));
        mapBuilder.put("secret", URLEncoder.encode(secret, StandardCharsets.UTF_8).replace("+", "%20"));
        mapBuilder.put("issuer", URLEncoder.encode(issuer, StandardCharsets.UTF_8).replace("+", "%20"));
        return StringSubstitutor.replace(qrCodeStr, mapBuilder.build());
    }

    public static boolean verification(String secretKey, String totpCode) {
        long time = System.currentTimeMillis()/1000/30;
        if (totpCode.equals(generateTOTP(secretKey, time))) {
            return true;
        }
        for (int i=-TIME_OFFSET; i <= TIME_OFFSET; i++) {
            if (i != 0) {
                if (totpCode.
                equals(generateTOTP(secretKey, time+i))) {
                    return true;
                }
            }
        }
        return false;
    }
}
