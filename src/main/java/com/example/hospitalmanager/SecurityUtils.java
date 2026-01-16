package com.example.hospitalmanager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecurityUtils {
    private static final String EMAIL_PATIENT = "^(?=.*[^@]*[0-9])(?=.*[^@]*[A-Z])[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATIENT);

    public static boolean isValidEmail(String email){
        if(email == null) return false;
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String hashPassword(String originalPassword){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodehash = digest.digest(originalPassword.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodehash);
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
    }
    private static String bytesToHex(byte[] hash){
        StringBuilder hexString = new StringBuilder(2*hash.length);
        for(int i = 0; i < hash.length; i++){
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length()==1) hexString.append(0);
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
