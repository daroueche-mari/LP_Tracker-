package util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils { // Classe utilitaire pour la gestion des mots de passe (ex: pour générer des sels, hasher les mots de passe, etc.)
    
    // Le POIVRE : Ne doit jamais être en base de données !
    private static final String PEPPER = "LP_TRACKER_SECRET_KEY_2026"; 

    public static String generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) { // Méthode pour hasher un mot de passe en utilisant SHA-256 avec un sel et un poivre (ex: lors de l'inscription ou de la vérification du mot de passe lors de la connexion)
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