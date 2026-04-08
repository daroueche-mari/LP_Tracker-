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
public void importFromCSV(String filePath) {
    String line;
    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(filePath))) {
        br.readLine(); // On saute l'en-tête
        while ((line = br.readLine()) != null) {
            // On sépare par la virgule
            String[] data = line.split(",");
            if (data.length >= 4) {
                String prenom = data[0].trim();
                String nom = data[1].trim();
                int age = Integer.parseInt(data[2].trim());
                
                // Sécurité : on remplace la virgule par un point pour le calcul
                double note = Double.parseDouble(data[3].trim().replace(",", "."));
                
                Student s = new Student(0, prenom, nom, age, note);
                addStudent(s);
            }
        }
        System.out.println("✅ Importation réussie !");
    } catch (Exception e) {
        System.err.println("❌ Erreur lors de l'import : " + e.getMessage());
    }
}
public void exportResultsToHTML(String filePath) {
    List<Student> students = getAllStudents();
    try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.File(filePath))) {
        writer.println("<html><head><title>Résultats LP Tracker</title></head><body>");
        writer.println("<h1>Rapport de Classe</h1>");
        writer.println("<table border='1'><tr><th>Nom</th><th>Note</th></tr>");
        
        for (Student s : students) {
            writer.println("<tr><td>" + s.getLastName() + " " + s.getFirstName() + "</td><td>" + s.getGrade() + "/20</td></tr>");
        }
        
        writer.println("</table>");
        writer.println("<p><b>" + getGlobalStats() + "</b></p>");
        writer.println("</body></html>");
        System.out.println("🌐 Rapport HTML généré : " + filePath);
    } catch (Exception e) { e.printStackTrace(); }
}
public String getDetailedStats() {
    String stats = "";
    // Requête pour la moyenne générale, le nombre total et la répartition par âge
    String sqlGlobal = "SELECT AVG(grade) as moyenne, COUNT(*) as total FROM student";
    String sqlGrouped = "SELECT age, COUNT(*) as nb FROM student GROUP BY age ORDER BY age";

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement()) {
        
        // 1. Stats Globales
        ResultSet rs1 = stmt.executeQuery(sqlGlobal);
        if (rs1.next()) {
            stats += String.format("Moyenne : %.2f/20 | Total : %d élèves\n", 
                                   rs1.getDouble("moyenne"), rs1.getInt("total"));
        }

        // 2. Répartition par âge (GROUP BY)
        ResultSet rs2 = stmt.executeQuery(sqlGrouped);
        stats += "Répartition : ";
        while (rs2.next()) {
            stats += rs2.getInt("age") + " ans (" + rs2.getInt("nb") + ") ";
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return stats;
}
public List<Student> findAdvanced(String name, Double minGrade, Integer age) {
    List<Student> list = new ArrayList<>();
    // Base de la requête
    StringBuilder sql = new StringBuilder("SELECT * FROM student WHERE 1=1");
    
    // On ajoute les filtres seulement s'ils sont remplis
    if (name != null && !name.isEmpty()) sql.append(" AND (first_name ILIKE ? OR last_name ILIKE ?)");
    if (minGrade != null) sql.append(" AND grade >= ?");
    if (age != null && age > 0) sql.append(" AND age = ?");

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
        
        int paramIndex = 1;
        if (name != null && !name.isEmpty()) {
            pstmt.setString(paramIndex++, "%" + name + "%");
            pstmt.setString(paramIndex++, "%" + name + "%");
        }
        if (minGrade != null) pstmt.setDouble(paramIndex++, minGrade);
        if (age != null && age > 0) pstmt.setInt(paramIndex++, age);

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            list.add(new Student(rs.getInt("id"), rs.getString("first_name"), 
                                 rs.getString("last_name"), rs.getInt("age"), 
                                 rs.getDouble("grade")));
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return list;
}
}