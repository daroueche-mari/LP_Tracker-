package dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;
import model.Student;

public class StudentDAO {

    /**
     * AJOUTER (Create)
     */
    public void addStudent(Student student) {
        String sql = "INSERT INTO student (first_name, last_name, age, grade) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setInt(3, student.getAge());
            pstmt.setDouble(4, student.getGrade());

            pstmt.executeUpdate();
            System.out.println("✅ [DAO] Étudiant inséré avec succès !");

        } catch (SQLException e) {
            System.err.println("❌ [DAO] Erreur lors de l'insertion : " + e.getMessage());
        }
    }

    /**
     * LIRE TOUT (Read)
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM student ORDER BY id ASC"; // Trié par ID pour la clarté

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Student s = new Student(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getInt("age"),
                    rs.getDouble("grade")
                );
                students.add(s);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur de lecture : " + e.getMessage());
        }
        return students;
    }

    /**
     * MODIFIER (Update) - AJOUTÉ
     */
    public void updateStudent(Student student) {
        String sql = "UPDATE student SET first_name = ?, last_name = ?, age = ?, grade = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setInt(3, student.getAge());
            pstmt.setDouble(4, student.getGrade());
            pstmt.setInt(5, student.getId()); // Crucial pour modifier la bonne ligne

            pstmt.executeUpdate();
            System.out.println("✅ [DAO] Étudiant ID " + student.getId() + " mis à jour !");

        } catch (SQLException e) {
            System.err.println("❌ [DAO] Erreur lors de la modification : " + e.getMessage());
        }
    }

    /**
     * SUPPRIMER (Delete) - AJOUTÉ
     */
    public void deleteStudent(int id) {
        String sql = "DELETE FROM student WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("✅ [DAO] Étudiant ID " + id + " supprimé !");

        } catch (SQLException e) {
            System.err.println("❌ [DAO] Erreur lors de la suppression : " + e.getMessage());
        }
    }
    public List<Student> searchStudents(String keyword) {
    List<Student> students = new ArrayList<>();
    // Recherche si le prénom OU le nom contient le mot-clé
    String sql = "SELECT * FROM student WHERE first_name ILIKE ? OR last_name ILIKE ? ORDER BY id ASC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        String pattern = "%" + keyword + "%"; // Exemple : "%ma%" trouvera "Marc" et "Damien"
        pstmt.setString(1, pattern);
        pstmt.setString(2, pattern);
        
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            students.add(new Student(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getInt("age"),
                rs.getDouble("grade")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return students;
}
    public String getGlobalStats() {
        String sql = "SELECT COUNT(*), AVG(grade) FROM student";
        try (Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int count = rs.getInt(1);
                double avg = rs.getDouble(2);
                return String.format("Total : %d étudiants | Moyenne Générale : %.2f/20", count, avg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Stats indisponibles";
    }
    public void exportToCSV(String filePath) {
    List<Student> students = getAllStudents();
    try (PrintWriter writer = new PrintWriter(new File(filePath))) {
        writer.println("Prenom,Nom,Age,Note"); // En-tête
        for (Student s : students) {
            writer.printf("%s,%s,%d,%.2f%n", s.getFirstName(), s.getLastName(), s.getAge(), s.getGrade());
        }
        System.out.println("✅ Export réussi dans " + filePath);
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }
}
}