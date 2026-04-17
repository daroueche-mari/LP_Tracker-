package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection { 
    // --- Configuration Supabase ---
    private static final String URL = "jdbc:postgresql://aws-0-eu-west-1.pooler.supabase.com:5432/postgres";
    private static final String USER = "postgres.stutavnnugpdbljdwidq"; 
    private static final String PASSWORD = "Azertyuiop@+13011"; 
    
    /**
     * MÉTHODE 1 : Établit la connexion avec la base de données
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL non trouvé !", e);
        }
    }

    /**
     * MÉTHODE 2 : Teste si la connexion est fonctionnelle
     * Utile au lancement de l'application pour alerter l'utilisateur si le serveur est DOWN
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("❌ Erreur de test de connexion : " + e.getMessage());
            return false;
        }
    }

    /**
     * MÉTHODE 3 : Ferme une connexion proprement
     * Sécurité supplémentaire pour libérer les ressources manuellement si besoin
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("🔌 Connexion à la base de données fermée.");
            } catch (SQLException e) {
                System.err.println("❌ Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
}