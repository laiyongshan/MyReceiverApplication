package com.lys.androidapp.myreceiverapplication.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Hjo on 2017/4/6.
 */
public class MD5Utils {

    /**
     * 将key值转为MD5
     *
     * @param key
     * @return
     */
    public static String String2MD5(String key) { //避免包含特殊字符时，文明命名不合法
        String CacheString = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(key.getBytes());
            CacheString = bytes2HexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            CacheString = String.valueOf(key.hashCode());
            e.printStackTrace();
        }
        return CacheString;
    }


    /**
     * encrypt32
     *
     * @param encryptStr
     * @return String
     */
    public static String encrypt32(String encryptStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(encryptStr.getBytes());
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
            encryptStr = hexValue.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return encryptStr.toUpperCase();
    }


    private static String bytes2HexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();

    }


    public static String MD5(String str) {
        MessageDigest md5 =null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch(Exception e) {
            e.printStackTrace();
            return "";
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray =new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue =new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) &0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }


    // 可逆的加密算法
    public static String encryptmd5(String str) {
        char[] a = str.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^'l');
        }
        String s = new String(a);
        return s;
    }
}
