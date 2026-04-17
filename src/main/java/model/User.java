package model;

public class User { // Classe modèle représentant un utilisateur de l'application (ex: pour stocker les informations d'un utilisateur connecté ou pour afficher les profils des utilisateurs)
    private int id;
    private String username;
    private String passwordHash;
    private String salt;
    private String avatarUrl;
    // --- CONSTRUCTEUR ET GETTERS/SETTERS ---
    // Constructeur complet pour créer un objet User à partir des données récupérées de la base de données (ex: lors de la connexion ou de l'affichage du profil)
    public User(int id, String username, String passwordHash, String salt, String avatarUrl) { // Constructeur complet pour créer un objet User à partir des données récupérées de la base de données (ex: lors de la connexion ou de l'affichage du profil)
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.avatarUrl = avatarUrl;
    }

    // Getters
    public int getId() { return id; } // Accolade rajoutée ici
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getSalt() { return salt; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getPassword() {
        return passwordHash;
    }

    public void setPassword(String password) { // Méthode pour mettre à jour le mot de passe hashé de l'utilisateur (ex: si on implémente une fonctionnalité de changement de mot de passe)
        this.passwordHash = password;
    }
}