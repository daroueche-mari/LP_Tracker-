package service;

import dao.StudentDAO;
import model.Student;
import java.util.List;

public class StudentService {
    private StudentDAO dao = new StudentDAO();

    /**
     * Calcule les statistiques globales formatées
     */
    public String getGlobalStatsString() {
        return dao.getGlobalStats(); // On garde l'appel DAO pour l'instant, mais on centralise ici
    }

    /**
     * Calcule la moyenne pour une liste spécifique (recherche/filtre)
     */
    public String getFormattedStatsForSelection(List<Student> students) {
        if (students == null || students.isEmpty()) {
            return "Aucun résultat trouvé pour ces critères.";
        }
        double avg = students.stream().mapToDouble(Student::getGrade).average().orElse(0.0);
        return String.format("Résultats : %d élèves | Moyenne du groupe : %.2f/20", 
                             students.size(), avg);
    }

    /**
     * Logique métier pour l'ajout (validation)
     */
    public boolean validateAndAdd(String fName, String lName, String ageStr, String gradeStr) {
        try {
            int age = Integer.parseInt(ageStr);
            double grade = Double.parseDouble(gradeStr);
            if (age < 0 || grade < 0 || grade > 20) return false;
            
            Student s = new Student(fName, lName, age, grade);
            dao.addStudent(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}