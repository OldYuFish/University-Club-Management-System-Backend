package com.wust.ucms.utils;

import org.springframework.util.StringUtils;

import java.util.Random;

public class CodeUtil {
    public static final String VERIFY_CODES = "1234567890";

    public static String generateVerifyCode() {
        return generateVerifyCode(VERIFY_CODES);
    }

    public static String generateVerifyCode(String sources) {
        if (StringUtils.hasText(sources)) {
            sources = VERIFY_CODES;
        }
        int codeLength = sources.length();
        Random random = new Random(System.currentTimeMillis());
        StringBuilder verifyCode = new StringBuilder(6);
        for (int i=0; i < 6; i++) {
            verifyCode.append(sources.charAt(random.nextInt(codeLength-1)));
        }
        return verifyCode.toString();
    }

    public static String generateGarbledCode() {
        char[] str = Long.toHexString(System.currentTimeMillis()).toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : str) {
            if (c >= '0' && c <= '9') {
                stringBuilder.append((char) (c+23));
            } else {
                stringBuilder.append(c);
            }
        }
        String s = stringBuilder.toString();
        return s;
    }
}
