package dao;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public boolean saveUser(String username, String hash, String salt, String avatarUrl) { // Méthode pour enregistrer un nouvel utilisateur dans la base de données (ex: lors de l'inscription, après que le service d'authentification ait généré le hash et le sel du mot de passe)
        String sql = "INSERT INTO users (username, password, salt, avatar_url) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, hash);
            pstmt.setString(3, salt);
            pstmt.setString(4, (avatarUrl != null && !avatarUrl.isEmpty()) ? avatarUrl : "default_avatar.png");
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur DAO (Inscription) : " + e.getMessage());
            return false;
        }
    }

    public User getUserByUsername(String username) { // Méthode pour récupérer un utilisateur de la base de données en fonction de son nom d'utilisateur (ex: lors de la connexion pour vérifier les informations d'identification)
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

    public List<User> getAllUsers() { // Méthode pour récupérer tous les utilisateurs de la base de données (ex: pour afficher une liste d'utilisateurs dans une section d'administration ou pour des fonctionnalités sociales)
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

    private User mapResultSetToUser(ResultSet rs) throws SQLException { // Méthode pour convertir une ligne de résultat SQL en un objet User (ex: lors de la récupération d'un utilisateur par son nom d'utilisateur)
        return new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("salt"),
            rs.getString("avatar_url") 
        );
    }
}