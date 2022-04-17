package at.kkhnifes.carshopbackend.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    public static String hashString(String text) {
        byte[] hashedValue = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            hashedValue = md.digest(text.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (hashedValue == null)
            return null;
        return new String(hashedValue);
    }
}
