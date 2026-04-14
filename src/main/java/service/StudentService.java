package service;

import dao.StudentDAO;
import exception.ValidationException;
import model.Student;
import java.util.List;

public class StudentService {
    private final StudentDAO studentDAO = new StudentDAO();

    /**
     * --- LOGIQUE DE CALCUL ---
     * Calcule la moyenne pour une liste spécifique (recherche/filtre)
     */
    public String getFormattedStatsForSelection(List<Student> students) {
        if (students == null || students.isEmpty()) {
            return "Aucun résultat trouvé pour ces critères.";
        }
        double avg = students.stream()
                             .mapToDouble(Student::getGrade)
                             .average()
                             .orElse(0.0);
                             
        return String.format("Résultats : %d élèves | Moyenne du groupe : %.2f/20", 
                             students.size(), avg);
    }

    /**
     * Récupère les statistiques globales formatées via le DAO
     */
    public String getGlobalStatsString() {
        return studentDAO.getGlobalStats();
    }

    /**
     * --- LOGIQUE MÉTIER & VALIDATION ---
     * Valide et ajoute un étudiant. Utilise ValidationException pour informer le Controller.
     */
    public void validateAndAdd(String fName, String lName, String ageStr, String gradeStr) throws ValidationException {
        // Nettoyage des entrées
        String first = (fName != null) ? fName.trim() : "";
        String last = (lName != null) ? lName.trim() : "";

        // 1. Validation de présence
        if (first.isEmpty() || last.isEmpty()) {
            throw new ValidationException("Le prénom et le nom sont obligatoires.");
        }

        // 2. Validation numérique et conversion
        int age;
        double grade;
        try {
            age = Integer.parseInt(ageStr.trim());
            // Supporte le point et la virgule pour les notes
            grade = Double.parseDouble(gradeStr.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new ValidationException("L'âge doit être un entier et la note un nombre valide.");
        }

        // 3. Règles métier strictes
        if (age < 18 || age > 70) {
            throw new ValidationException("L'âge doit être compris entre 18 et 70 ans.");
        }
        if (grade < 0 || grade > 20) {
            throw new ValidationException("La note doit être comprise entre 0 et 20.");
        }

        // 4. Appel au DAO si tout est OK
        studentDAO.addStudent(new Student(0, first, last, age, grade));
    }

    /**
     * --- LOGIQUE DE MISE À JOUR ---
     */
    public void validateAndUpdate(Student selected, String f, String l, String a, String g) throws ValidationException {
        // On réutilise la logique de parsing pour vérifier les modifs
        try {
            int age = Integer.parseInt(a.trim());
            double grade = Double.parseDouble(g.trim().replace(",", "."));
            
            if (age < 18 || grade < 0 || grade > 20) throw new Exception();

            selected.setFirstName(f.trim());
            selected.setLastName(l.trim());
            selected.setAge(age);
            selected.setGrade(grade);

            studentDAO.updateStudent(selected);
        } catch (Exception e) {
            throw new ValidationException("Mise à jour échouée : vérifiez les valeurs saisies.");
        }
    }
    /**
     * Supprime un étudiant via son ID en passant par le DAO.
     * Cette méthode permet au Controller de ne plus parler directement au DAO.
     */
    public void deleteStudent(int id) {
        studentDAO.deleteStudent(id);
    }
}