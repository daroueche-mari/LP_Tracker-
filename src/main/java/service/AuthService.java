package service;
// Importations des classes nécessaires pour le fonctionnement du service d'authentification (ex: pour interagir avec le DAO, manipuler les modèles d'utilisateur, gérer les exceptions liées à l'authentification, etc.)
import dao.UserDAO;
import model.User;
import util.PasswordUtils;
import exception.AuthException;
import java.util.regex.Pattern;

public class AuthService {

    private final UserDAO userDAO = new UserDAO(); // On lie le service au DAO

    // Regex : Min 8 caractères, 1 Majuscule, 1 Minuscule, 1 Chiffre, 1 Caractère spécial
    private static final String PASSWORD_PATTERN = 
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    /**
     * Méthode principale de connexion
     */
    // Elle prend le nom d'utilisateur et le mot de passe en entrée, vérifie les informations d'identification, et renvoie l'objet User correspondant si la connexion est réussie. Sinon, elle lance une AuthException avec un message d'erreur approprié.
    public User login(String username, String password) throws AuthException {
        User user = userDAO.getUserByUsername(username);

        if (user == null) {
            throw new AuthException("Utilisateur non trouvé.");
        }

        // On utilise ta méthode de vérification
        if (!verifyLogin(password, user.getPasswordHash(), user.getSalt())) {
            throw new AuthException("Mot de passe incorrect.");
        }

        return user; // Si tout est OK, on renvoie l'utilisateur
    }

    /**
     * Vérifie si le mot de passe respecte les critères de sécurité
     */
    public void validatePasswordComplexity(String password) throws AuthException {
        if (password == null || !Pattern.matches(PASSWORD_PATTERN, password)) {
            throw new AuthException("Le mot de passe doit contenir au moins 8 caractères, " +
                                   "une majuscule, une minuscule, un chiffre et un caractère spécial.");
        }
    }

    /**
     * Vérifie la connexion d'un utilisateur
     */
    public boolean verifyLogin(String passwordInput, String storedHash, String storedSalt) {
        String hashAttempt = PasswordUtils.hashPassword(passwordInput, storedSalt);
        return hashAttempt.equals(storedHash);
    }
    public void register(String username, String password) throws AuthException {
    // 1. On vérifie si l'utilisateur existe déjà
    if (userDAO.getUserByUsername(username) != null) {
        throw new AuthException("Cet identifiant est déjà utilisé.");
    }

    // 2. On valide la complexité du mot de passe
    validatePasswordComplexity(password);

    // 3. Sécurité : Génération du Sel et du Hash
    String salt = PasswordUtils.generateSalt();
    String hash = PasswordUtils.hashPassword(password, salt);

    // 4. Enregistrement en base via le DAO
    boolean success = userDAO.saveUser(username, hash, salt, "default_avatar.png");
    // 5. Si l'enregistrement échoue, on lance une exception
    if (!success) {
        throw new AuthException("Erreur lors de la création du compte.");
    }
}
}