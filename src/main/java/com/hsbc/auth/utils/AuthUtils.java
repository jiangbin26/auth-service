package com.hsbc.auth.utils;

import com.hsbc.auth.exception.AuthException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;


public class AuthUtils {

    private static final String AES = "AES";

    private static String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    private static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    public static String encode(String salt, String original) throws AuthException {
        try {
            byte[] bytekey = hexStringToByteArray(salt);
            SecretKeySpec sks = new SecretKeySpec(bytekey, AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
            byte[] encrypted = cipher.doFinal(original.getBytes());
            return byteArrayToHexString(encrypted);
        } catch (Exception e) {
            throw new AuthException("Failed to encode the password.");
        }
    }

    public static String decode(String salt, String encrypted) throws AuthException{
        try {
            byte[] bytekey = hexStringToByteArray(salt);
            SecretKeySpec sks = new SecretKeySpec(bytekey, AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, sks);
            byte[] decrypted = cipher.doFinal(hexStringToByteArray(encrypted));
            String originalPassword = new String(decrypted);
            return originalPassword;
        } catch (Exception e) {
            throw new AuthException("Failed to decode the encrypted password.");
        }
    }

    public static String generateAuthToken(){
        return new SecureRandom().ints(0,36)
                .mapToObj(i -> Integer.toString(i, 36))
                .map(String::toUpperCase).distinct().limit(16).collect(Collectors.joining())
                .replaceAll("([A-Z0-9]{4})", "$1-").substring(0,19);
    }

    public static Long getTokenExpiryTime(int expiryHours){
        return LocalDateTime.now().minusHours((-1) * expiryHours).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static boolean isTokenExpired(long expiryTime){
        long nowInEpochMill = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return nowInEpochMill > expiryTime;
    }
}
