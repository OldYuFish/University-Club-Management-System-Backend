package com.wust.ucms.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public static String calcMD5(MultipartFile file) {
        try {
            byte[] uploadBytes = file.getBytes();
            String md5Hex = DigestUtils.md5Hex(uploadBytes);
            String md5Stream = DigestUtils.md5Hex(file.getInputStream());
            String md5String = DigestUtils.md5Hex("字符串");
            return md5Hex;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String calcMD5(InputStream stream) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192];
            int len;
            while ((len = stream.read(buf)) > 0) {
                digest.update(buf, 0, len);
            }
            return toHexString(digest.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String toHexString(byte[] data) {
        StringBuilder r = new StringBuilder(data.length*2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }
}
