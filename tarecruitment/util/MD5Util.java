package com.group4.tarecruitment.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类，实现密码存储加密（TA-001验收标准5）
 */
public class MD5Util {
    // 盐值，增强加密安全性
    private static final String SALT = "BUPT_TA_RECRUITMENT_2026";

    public static String encrypt(String password) {
        try {
            // 拼接盐值
            String str = password + SALT;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] byteDigest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : byteDigest) {
                if (Integer.toHexString(0xff & b).length() == 1) {
                    sb.append("0").append(Integer.toHexString(0xff & b));
                } else {
                    sb.append(Integer.toHexString(0xff & b));
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 测试加密
    public static void main(String[] args) {
        System.out.println(encrypt("123456")); // 输出加密后的密码
    }
}