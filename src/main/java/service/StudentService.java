package service;
// Importations des classes nécessaires pour le fonctionnement du service de gestion des étudiants (ex: pour interagir avec le DAO, manipuler les modèles d'étudiant, gérer les exceptions liées à la validation des données, etc.)
import dao.StudentDAO;
import exception.ValidationException;
import model.Student;
import java.util.List;

public class StudentService {
    // 1. On enlève le "final" et le "new StudentDAO()"
    private StudentDAO studentDAO;

    // 2. Constructeur par défaut (pour l'application réelle)
    public StudentService() {
        this.studentDAO = new StudentDAO();
    }

    // 3. Constructeur pour les tests (permet à Mockito d'injecter le mock)
    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }
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
        // 1. Nettoyage et Capitalisation (ex: "jean" devient "Jean")
        String first = (fName != null && !fName.trim().isEmpty()) ? formatName(fName.trim()) : "";
        String last = (lName != null && !lName.trim().isEmpty()) ? formatName(lName.trim()) : "";

        // 2. Validation de présence et de format (Pas de chiffres)
        // Cette regex autorise les lettres, les accents, les espaces et les tirets.
        String nameRegex = "^[a-zA-ZÀ-ÿ\\s-]+$";

        if (first.isEmpty() || last.isEmpty()) {
            throw new ValidationException("Le prénom et le nom sont obligatoires.");
        }
        if (!first.matches(nameRegex) || !last.matches(nameRegex)) {
            throw new ValidationException("Le nom et le prénom doivent contenir que des lettres.");
        }

        // 3. Validation de l'âge
        int age;
        try {
            age = Integer.parseInt(ageStr.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("L'âge doit être un nombre entier valide.");
        }

        if (age < 18 || age > 60) {
            throw new ValidationException("L'âge doit être compris entre 18 et 60 ans.");
        }

        // 4. Validation de la note (OPTIONNELLE)
        double grade = 0.0; // Valeur par défaut
        if (gradeStr != null && !gradeStr.trim().isEmpty()) {
            try {
                grade = Double.parseDouble(gradeStr.trim().replace(",", "."));
                if (grade < 0 || grade > 20) {
                    throw new ValidationException("La note doit être comprise entre 0 et 20.");
                }
            } catch (NumberFormatException e) {
                throw new ValidationException("La note saisie n'est pas un nombre valide.");
            }
        }

        // 5. Création de l'objet et Appel au DAO
        // On crée d'abord l'objet et on le stocke dans la variable 'newStudent'
        Student newStudent = new Student(0, first, last, age, grade);

        // On appelle le DAO UNE SEULE FOIS et on récupère le résultat
        boolean success = studentDAO.addStudent(newStudent);

        if (!success) {
            throw new ValidationException("L'insertion en base de données a échoué.");
        }
    }

    /**
     * Méthode utilitaire pour mettre la première lettre en majuscule
     */
    private String formatName(String input) {
        if (input == null || input.isEmpty()) return "";
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
    /**
     * --- LOGIQUE DE MISE À JOUR ---
     */
    // Valide et met à jour un étudiant existant. Utilise ValidationException pour informer le Controller en cas de problème.
    public void validateAndUpdate(Student selected, String f, String l, String a, String g) throws ValidationException {
        // On réutilise la logique de parsing pour vérifier les modifs
        try {
            int age = Integer.parseInt(a.trim());
            double grade = Double.parseDouble(g.trim().replace(",", "."));
            // Règles métier strictes
            if (age < 18 || grade < 0 || grade > 20) throw new Exception();
            // Si tout est OK, on met à jour l'étudiant sélectionné
            selected.setFirstName(f.trim());
            selected.setLastName(l.trim());
            selected.setAge(age);
            selected.setGrade(grade);
            // Appel au DAO pour mettre à jour
            studentDAO.updateStudent(selected);
        } catch (Exception e) {
            throw new ValidationException("Mise à jour échouée : vérifiez les valeurs saisies.");
        }
    }
    // --- SUPPRESSION UNIQUE (mise à jour) ---
    public void deleteStudent(int id) {
        // On transforme l'ID unique en une liste contenant un seul élément
        // pour pouvoir appeler la nouvelle méthode du DAO
        studentDAO.deleteMultiple(List.of(id));
    }

    // --- SUPPRESSION MULTIPLE (ajout) ---
    public void deleteMultipleStudents(List<Integer> ids) {
        studentDAO.deleteMultiple(ids);
    }
}