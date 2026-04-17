package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Student;
import util.QueryExecutor;

public class StudentDAO {

    // --- MÉTHODE UTILITAIRE (Mappage SQL -> Objet) ---
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
            rs.getInt("id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getInt("age"),
            rs.getDouble("grade")
        );
    }

    // --- ACTIONS CRUD (Utilisent QueryExecutor pour la sécurité) ---
    // Méthode d'ajout d'un étudiant. Elle prend un objet Student, construit la requête SQL d'insertion, et utilise QueryExecutor pour l'exécuter en toute sécurité.
    public boolean addStudent(Student student) {
        String sql = "INSERT INTO student (first_name, last_name, age, grade) VALUES (?, ?, ?, ?)";
        
        int result = QueryExecutor.executeUpdate(sql, 
            student.getFirstName(), 
            student.getLastName(), 
            student.getAge(), 
            student.getGrade()
        );

        return result > 0; // Retourne true si une ligne a été créée
    }
    // Méthode de mise à jour d'un étudiant. Elle prend un objet Student avec un ID existant, construit la requête SQL de mise à jour, et utilise QueryExecutor pour l'exécuter en toute sécurité.
    public void updateStudent(Student student) {
        String sql = "UPDATE student SET first_name = ?, last_name = ?, age = ?, grade = ? WHERE id = ?";
        QueryExecutor.executeUpdate(sql, 
            student.getFirstName(), 
            student.getLastName(), 
            student.getAge(), 
            student.getGrade(), 
            student.getId()
        );
        System.out.println("✅ [DAO] Étudiant mis à jour.");
    }

    

    // --- LECTURE ET RECHERCHE (Gérées classiquement pour le ResultSet) ---
    // Méthode pour récupérer tous les étudiants de la base de données. Elle exécute une requête SQL de sélection, parcourt le ResultSet, convertit chaque ligne en un objet Student, et retourne la liste complète.
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM student ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return students;
    }
    // Méthode pour récupérer un étudiant spécifique en fonction de son ID. Elle exécute une requête SQL de sélection avec une condition WHERE, vérifie si un résultat est retourné, et convertit la ligne en un objet Student.
    public Student getStudentById(int id) {
        String sql = "SELECT * FROM student WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return mapResultSetToStudent(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    // Méthode de recherche avancée. Elle prend des critères de recherche (prénom, nom, âge), construit dynamiquement la requête SQL en fonction des critères fournis, exécute la requête, et retourne la liste des étudiants correspondants.
    public List<Student> findAdvanced(String firstName, String lastName, Integer age) {
        List<Student> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM student WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (firstName != null && !firstName.isEmpty()) {
            sql.append(" AND first_name ILIKE ?");
            params.add("%" + firstName + "%");
        }
        if (lastName != null && !lastName.isEmpty()) {
            sql.append(" AND last_name ILIKE ?");
            params.add("%" + lastName + "%");
        }
        if (age != null && age > 0) {
            sql.append(" AND age = ?");
            params.add(age);
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) { list.add(mapResultSetToStudent(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- PAGINATION ET STATS (Méthodes de calcul) ---
    // Méthode pour récupérer une page d'étudiants. Elle prend le numéro de page et la taille de page, calcule l'offset, exécute une requête SQL avec LIMIT et OFFSET, et retourne la liste des étudiants pour cette page.
    public List<Student> getStudentsPaged(int page, int size) {
        List<Student> list = new ArrayList<>();
        int offset = page * size; 
        String sql = "SELECT * FROM student ORDER BY id LIMIT ? OFFSET ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, size);
            pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    // Méthode pour récupérer les statistiques globales. Elle exécute une requête SQL qui utilise COUNT et AVG pour obtenir le nombre total d'étudiants et la moyenne des notes, ainsi qu'une requête GROUP BY pour obtenir la répartition par âge, puis formate ces informations dans une chaîne de caractères.
    public String getGlobalStats() {
        String sqlGlobal = "SELECT COUNT(*), AVG(grade) FROM student"; 
        String sqlGrouped = "SELECT age, COUNT(*) as nb FROM student GROUP BY age ORDER BY age"; 
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            StringBuilder stats = new StringBuilder();
            ResultSet rs1 = stmt.executeQuery(sqlGlobal);
            if (rs1.next()) {
                stats.append(String.format("📊 Total : %d élèves | Moyenne : %.2f/20\n", 
                             rs1.getInt(1), rs1.getDouble(2)));
            }

            ResultSet rs2 = stmt.executeQuery(sqlGrouped);
            stats.append("👥 Répartition : ");
            while (rs2.next()) {
                stats.append(rs2.getInt("age")).append(" ans (").append(rs2.getInt("nb")).append(")  ");
            }
            return stats.toString();
        } catch (SQLException e) { return "Stats indisponibles"; }
    }

   // --- SUPPRESSION MULTIPLE ---
   // Méthode pour supprimer plusieurs étudiants en fonction d'une liste d'IDs. Elle construit une requête SQL avec une clause IN, et utilise QueryExecutor pour l'exécuter en toute sécurité.
public void deleteMultiple(List<Integer> ids) {
    if (ids == null || ids.isEmpty()) return;
    
    // On prépare la liste d'IDs : "1, 2, 3"
    String idList = ids.stream()
                       .map(String::valueOf)
                       .collect(java.util.stream.Collectors.joining(","));
                       
    // On construit la requête (Attention : vérifie si c'est 'student' ou 'students')
    String sql = "DELETE FROM student WHERE id IN (" + idList + ")";
    
    // On utilise QueryExecutor qui gère tout le travail SQL
    QueryExecutor.executeUpdate(sql);
}
// --- IMPORTATION EN BATCH (Performance) ---
// Méthode pour ajouter une liste d'étudiants en une seule opération. Elle prend
public void addStudentsBatch(List<Student> students) {
    String sql = "INSERT INTO student (first_name, last_name, age, grade) VALUES (?, ?, ?, ?)";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        // On désactive l'auto-commit pour la rapidité
        conn.setAutoCommit(false);

        for (Student s : students) {
            pstmt.setString(1, s.getFirstName());
            pstmt.setString(2, s.getLastName());
            pstmt.setInt(3, s.getAge());
            pstmt.setDouble(4, s.getGrade());
            pstmt.addBatch(); // On ajoute à la pile, on n'envoie pas encore
        }

        pstmt.executeBatch(); // On envoie tout d'un coup !
        conn.commit();        // On valide la transaction
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}