package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Student;

public class StudentDAO {

    // --- MÉTHODE UTILITAIRE (Pour éviter de répéter le mappage) ---
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
            rs.getInt("id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getInt("age"),
            rs.getDouble("grade")
        );
    }

    // --- ACTIONS CRUD ---

    public void addStudent(Student student) {
        String sql = "INSERT INTO student (first_name, last_name, age, grade) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setInt(3, student.getAge());
            pstmt.setDouble(4, student.getGrade());
            pstmt.executeUpdate();
            System.out.println("✅ [DAO] Étudiant inséré.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

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

    public void updateStudent(Student student) {
        String sql = "UPDATE student SET first_name = ?, last_name = ?, age = ?, grade = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setInt(3, student.getAge());
            pstmt.setDouble(4, student.getGrade());
            pstmt.setInt(5, student.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteStudent(int id) {
        String sql = "DELETE FROM student WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- RECHERCHES ET FILTRES ---

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

    public List<Student> findAdvanced(String firstName, String lastName, Integer age) {
        List<Student> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM student WHERE 1=1");
        
        if (firstName != null && !firstName.isEmpty()) sql.append(" AND first_name ILIKE ?");
        if (lastName != null && !lastName.isEmpty()) sql.append(" AND last_name ILIKE ?");
        if (age != null && age > 0) sql.append(" AND age = ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (firstName != null && !firstName.isEmpty()) pstmt.setString(idx++, "%" + firstName + "%");
            if (lastName != null && !lastName.isEmpty()) pstmt.setString(idx++, "%" + lastName + "%");
            if (age != null && age > 0) pstmt.setInt(idx++, age);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) { list.add(mapResultSetToStudent(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- PAGINATION ET STATS ---

  public List<Student> getStudentsPaged(int page, int size) {
    List<Student> list = new ArrayList<>();
    // Calcul de l'offset : Si page 0 -> offset 0. Si page 1 -> offset 20.
    int offset = page * size; 
    
    String sql = "SELECT * FROM student ORDER BY id LIMIT ? OFFSET ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, size);   // La limite (combien d'étudiants on veut)
        pstmt.setInt(2, offset); // Le point de départ (offset)
        
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            list.add(mapResultSetToStudent(rs));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}

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
}