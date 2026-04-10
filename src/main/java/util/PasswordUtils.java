package util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {
    
    // Le POIVRE : Ne doit jamais être en base de données !
    private static final String PEPPER = "LP_TRACKER_SECRET_KEY_2026"; 

    public static String generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) {
        try {
            // SHA-256 avec Sel + Poivre
            String dataToHash = salt + password + PEPPER;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(dataToHash.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encodedhash);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du hashage");
        }
    }
}