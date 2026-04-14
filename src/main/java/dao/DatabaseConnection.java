package dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection { 
    // --- Configuration de la connexion à la base de données ---
    private static final String URL = "jdbc:postgresql://localhost:5432/student_management";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException { // Méthode pour obtenir une connexion à la base de données PostgreSQL
        try {
            // Force le chargement du driver PostgreSQL
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL non trouvé !", e);
        }
    }
}