package com.ustrike.util;

import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHasher {
    private static final String ALGO = "PBKDF2WithHmacSHA256";
    private static final int ITER = 65536;
    
    public static String hash(String pass) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        
        char[] chars = pass.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(chars, salt, ITER, 256);
        
        try {
            byte[] hash = SecretKeyFactory.getInstance(ALGO)
                .generateSecret(spec).getEncoded();
            return encodeHex(salt) + ":" + encodeHex(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static boolean verify(String pass, String hash) {
        String[] parts = hash.split(":");
        byte[] salt = decodeHex(parts[0]);
        byte[] expected = decodeHex(parts[1]);
        
        char[] chars = pass.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(chars, salt, ITER, expected.length * 8);
        
        try {
            byte[] actual = SecretKeyFactory.getInstance(ALGO)
                .generateSecret(spec).getEncoded();
            return slowEquals(actual, expected);
        } catch (Exception e) {
            return false;
        }
    }
    
    private static String encodeHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
    
    private static byte[] decodeHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return bytes;
    }
    
    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}
