package dao;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // 1. Sauvegarder (Inscription)
    // J'ai renommé la méthode pour plus de clarté
    public boolean saveUser(String username, String hash, String salt, String avatarUrl) {
        // On cible maintenant la table 'users'
        String sql = "INSERT INTO users (username, password, salt, avatar_url) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, hash); // Stocké dans la colonne 'password'
            pstmt.setString(3, salt);
            pstmt.setString(4, (avatarUrl != null && !avatarUrl.isEmpty()) ? avatarUrl : "default_avatar.png");
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur DAO (Inscription) : " + e.getMessage());
            return false;
        }
    }

    // 2. Trouver par nom (Connexion)
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur DAO (FindUser) : " + e.getMessage());
        }
        return null;
    }

    // 3. Lister tous les utilisateurs
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur DAO (ListUsers) : " + e.getMessage());
        }
        return users;
    }

    // Méthode interne mise à jour pour correspondre aux colonnes de 'users'
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password"), // Correspond à la colonne 'password' de ta table
            rs.getString("salt"),
            // Si tu n'as pas encore ajouté avatar_url en SQL, commente la ligne suivante
            rs.getString("avatar_url") 
        );
    }
}