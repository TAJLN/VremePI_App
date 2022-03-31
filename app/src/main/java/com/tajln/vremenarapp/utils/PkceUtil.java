package com.tajln.vremenarapp.utils;

import android.os.Build;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PkceUtil {
    public static String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] codeVerifier = new byte[32];
        secureRandom.nextBytes(codeVerifier);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
        }
        return null;
    }
    public static String generateCodeChallange(String codeVerifier) {
        try {
            byte[] bytes = codeVerifier.getBytes("US-ASCII");
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(bytes, 0, bytes.length);
            byte[] digest = messageDigest.digest();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}