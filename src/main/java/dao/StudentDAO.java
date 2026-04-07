package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import model.Student;

public class StudentDAO {

    /**
     * Ajoute un étudiant dans la base de données PostgreSQL
     */
    public void addStudent(Student student) {
        // La requête SQL avec des "?" pour la sécurité (évite les injections SQL)
        String sql = "INSERT INTO student (first_name, last_name, age, grade) VALUES (?, ?, ?, ?)";

        // try-with-resources : ferme automatiquement la connexion et le statement
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // On remplace les "?" par les vraies valeurs de l'objet Student
            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setInt(3, student.getAge());
            pstmt.setDouble(4, student.getGrade());

            // Exécution de la requête
            pstmt.executeUpdate();
            System.out.println("✅ [DAO] Étudiant inséré avec succès !");

        } catch (SQLException e) {
            System.err.println("❌ [DAO] Erreur lors de l'insertion : " + e.getMessage());
            e.printStackTrace();
        }
    }
}