package model;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String salt;
    private String avatarUrl;

    public User(int id, String username, String passwordHash, String salt, String avatarUrl) {
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
}