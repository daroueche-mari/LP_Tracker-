package exception;

public class AuthException extends Exception { // Exception personnalisée pour les erreurs d'authentification (ex: "Utilisateur non trouvé", "Mot de passe incorrect", etc.)
    public AuthException(String message) {
        super(message);
    }
}