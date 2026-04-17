package util;

import java.sql.*;
import dao.DatabaseConnection;

public class QueryExecutor {

    // Pour INSERT, UPDATE, DELETE
    // Cette méthode générique prend une requête SQL avec des paramètres, les remplace de manière sécurisée, et exécute la requête. Elle retourne le nombre de lignes affectées ou -1 en cas d'erreur.
    public static int executeUpdate(String sql, Object... params) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            // On logue l'erreur technique pour le dev
            System.err.println("❌ [QueryExecutor] Erreur SQL : " + e.getMessage());
            return -1; // On retourne -1 pour indiquer une vraie erreur technique
        }
    }

    // Pour les SELECT (Méthode générique)
    // Utile pour fermer la connexion proprement après lecture
    // Cette méthode générique prend une requête SQL de sélection, des paramètres, et un handler pour traiter le ResultSet. Elle exécute la requête et laisse le handler gérer les résultats.
    public static void executeQuery(String sql, ResultSetHandler handler, Object... params) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                handler.handle(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ [QueryExecutor] Erreur Lecture : " + e.getMessage());
        }
    }
    // Interface fonctionnelle pour le handler de ResultSet, permettant de traiter les résultats d'une requête de manière flexible et réutilisable.
    @FunctionalInterface
    public interface ResultSetHandler {
        void handle(ResultSet rs) throws SQLException;
    }
}