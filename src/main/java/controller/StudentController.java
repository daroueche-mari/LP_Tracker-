package controller;

import dao.StudentDAO;
import exception.ValidationException;
import model.Student;
import service.ExportService;
import service.StudentService;
import view.AppView;
import view.StatChartView;
import util.UIUtils;

import java.util.List;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
// Importations des classes nécessaires pour le fonctionnement du controller de gestion des étudiants (ex: pour interagir avec la vue, appeler les méthodes du service, gérer les événements liés à l'interface utilisateur, etc.)
public class StudentController {

    @FXML private Object fNameInput, lNameInput, ageInput, gradeInput, searchField;
    @FXML private Object searchFirstName, searchLastName;
    @FXML private Object studentTable;
    @FXML private Object idColumn, colFirstName, colLastName, colAge, colGrade;
    @FXML private Object ageFilterCombo;
    @FXML private Object statsCount, statsAverage;

    private final StudentDAO studentDAO = new StudentDAO();
    private final ExportService exportService = new ExportService();
    private StudentService studentService;
    private List<Student> masterData = new java.util.ArrayList<>();

    public StudentController() {
        this.studentService = new StudentService(); // Valeur par défaut pour l'appli
    }

    // Ce setter permettra au test de remplacer le vrai service par le faux
    public void setStudentService(StudentService studentService) {
        this.studentService = studentService;
    }
    @FXML // Méthode d'initialisation du controller. Elle est appelée automatiquement par JavaFX après le chargement de la vue. C'est ici que tu configures ton tableau, tes filtres, et que tu charges les données initiales.
    public void initialize() { 
        // Configuration initiale via AppView
        AppView.setupTable(studentTable, idColumn, colFirstName, colLastName, colAge, colGrade);
        AppView.setupFilters(ageFilterCombo, searchFirstName, searchLastName, this::onFilterChange);
        // AppView.setupScroll(studentTable, () -> { if (!isLoading) loadNextPage(); });
        AppView.optimizeTablePerformance(studentTable);    
        AppView.enableMultipleSelection(studentTable);
        refreshTable();
        AppView.enableMultipleSelection(studentTable);
    }
    // Méthode pour afficher les statistiques globales (total d'élèves, moyenne générale, répartition par âge) en utilisant une requête SQL GROUP BY dans le DAO.
    private void refreshTable() {
        masterData = studentDAO.getAllStudents(); 
        AppView.setTableItems(studentTable, masterData);
        updateStatistics();
    }
    // Méthode appelée à chaque changement de filtre (texte ou ComboBox). Elle applique les filtres sur 'masterData' et met à jour la TableView et les statistiques.
    private void onFilterChange() {
        AppView.filterTable(
            studentTable, 
            masterData, 
            AppView.getText(searchFirstName), 
            AppView.getText(searchLastName), 
            AppView.getValue(ageFilterCombo)
        );
        updateStatistics();
    }
    // --- GESTION DES ÉVÉNEMENTS (Appelent les méthodes du service et mettent à jour la vue) ---
    // Méthode d'ajout d'un étudiant. Elle récupère les données du formulaire, valide et ajoute l'étudiant via le service, puis rafraîchit la TableView.
    @FXML
    private void handleAddStudent() {
        try {
            // 1. On récupère les textes bruts
            String fName = AppView.getText(fNameInput);
            String lName = AppView.getText(lNameInput);
            String ageStr = AppView.getText(ageInput);
            String gradeStr = AppView.getText(gradeInput);

            // 2. On appelle le SERVICE (c'est lui qui fait tout le boulot)
            // Assure-toi d'avoir injecté ton service : private StudentService studentService = new StudentService();
            studentService.validateAndAdd(fName, lName, ageStr, gradeStr);

            // 3. Si on arrive ici, c'est que ça a marché !
            AppView.notifySuccess("Étudiant ajouté avec succès !");
            refreshTable();
            AppView.clearFields(fNameInput, lNameInput, ageInput, gradeInput);
            autoSave();

        } catch (ValidationException e) {
            // Ici, on attrape tes messages personnalisés ("L'âge doit être...", etc.)
            AppView.notifyWarning("Validation", e.getMessage());
        } catch (Exception e) {
            // Ici, on attrape les erreurs imprévues (BDD, etc.)
            AppView.notifyError("Erreur système", "Une erreur est survenue : " + e.getMessage());
        }
    }

// Méthode de sauvegarde automatique silencieuse
private void autoSave() {
    try {
        String path = "autosave_tracker.csv";
        exportService.exportToCSV(AppView.getTableItems(studentTable), path);
        System.out.println("☁️ [AutoSave] Données sauvegardées dans " + path);
    } catch (Exception e) {
        System.err.println("⚠️ Échec de la sauvegarde automatique.");
    }
}
    
    @FXML // Méthode de mise à jour d'un étudiant sélectionné. Elle récupère les données du formulaire, valide et met à jour l'étudiant via le service.
    private void handleUpdateStudent() {
        Student selected = (Student) AppView.getSelectedItem(studentTable);
        if (selected == null) {
            AppView.notifyWarning("Sélection", "Veuillez choisir un étudiant.");
            return;
        }
        try {
            studentService.validateAndUpdate(selected, 
                AppView.getText(fNameInput), AppView.getText(lNameInput), 
                AppView.getText(ageInput), AppView.getText(gradeInput));
            AppView.notifySuccess("Données mises à jour.");
            refreshTable();
        } catch (ValidationException e) {
            AppView.notifyError("Erreur", e.getMessage());
        }
    }


@FXML // Méthode de suppression d'un ou plusieurs étudiants sélectionnés. Elle récupère les IDs des étudiants sélectionnés et les supprime via le service.
private void handleDelete() {
    // 1. On utilise l'utilitaire pour récupérer la liste proprement
    List<Student> selectedStudents = AppView.getSelectedStudents(studentTable);

    // 2. Maintenant 'selectedStudents' est une vraie List, donc isEmpty() fonctionne !
    if (selectedStudents.isEmpty()) {
        AppView.notifyWarning("Sélection vide", "Veuillez sélectionner au moins un étudiant.");
        return;
    }

    int count = selectedStudents.size();
    String title = (count == 1) ? "Suppression unique" : "Suppression multiple";
    
    // 3. Accès aux éléments de la liste
    String message = (count == 1) 
        ? "Voulez-vous supprimer " + selectedStudents.get(0).getFirstName() + " " + selectedStudents.get(0).getLastName() + " ?"
        : "Voulez-vous supprimer ces " + count + " étudiants ?";

    if (AppView.showConfirmation(title, message)) {
        try {
            // 4. Extraction des IDs (stream() fonctionne sur une List !)
            List<Integer> ids = selectedStudents.stream()
                                                .map(Student::getId)
                                                .toList();
            
            studentDAO.deleteMultiple(ids);
            refreshTable();
            AppView.notifySuccess(count + " étudiant(s) supprimé(s).");
            
        } catch (Exception e) {
            AppView.notifyError("Erreur", "Impossible de supprimer la sélection.");
        }
    }
}

    @FXML // Méthode de recherche d'un étudiant par ID. Elle récupère l'ID saisi, valide, cherche l'étudiant via le DAO et affiche les résultats.
    private void handleSearchById() {
        String idText = AppView.getText(searchField);
        
        if (idText.isEmpty()) {
            AppView.notifyWarning("Champ vide", "Veuillez saisir un ID pour la recherche.");
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            Student s = studentDAO.getStudentById(id);
            
            if (s != null) {
                // 1. Remplissage automatique du formulaire
                AppView.fillForm(s, fNameInput, lNameInput, ageInput, gradeInput);
                
                // 2. Sélection visuelle dans le tableau via l'utilitaire AppView
                // Plus besoin de "instanceof" ou de "TableView" ici !
                AppView.selectAndScroll(studentTable, s);
                
                // 3. Affichage des détails complets
                String fullInfo = String.format(
                    "✅ Étudiant trouvé (ID: %d)\n" +
                    "----------------------------------\n" +
                    "• Prénom : %s\n" +
                    "• Nom : %s\n" +
                    "• Âge : %d ans\n" +
                    "• Note : %.2f / 20",
                    s.getId(), s.getFirstName(), s.getLastName(), s.getAge(), s.getGrade()
                );
                AppView.notifySuccess(fullInfo);
                
            } else {
                AppView.notifyError("Introuvable", "Aucun étudiant ne correspond à l'ID " + id);
            }
        } catch (NumberFormatException e) {
            AppView.notifyError("Erreur de format", "L'ID doit être un nombre entier.");
        }
    }
    // --- STATISTIQUES ---
    // Méthode pour afficher les statistiques globales (total d'élèves, moyenne générale, répartition par âge) en utilisant une requête SQL GROUP BY dans le DAO.
  @FXML
private void handleImportCSV() { 
    String path = AppView.showOpenDialog(studentTable);
    if (path != null) {
        // Ajout d'une confirmation avant l'importation massive
        if (AppView.showConfirmation("Confirmation d'import", "Voulez-vous importer les données de ce fichier ?")) {
            try {
                exportService.importFromCSV(path, studentDAO);
                refreshTable();
                AppView.notifySuccess("Importation réussie !");
            } catch (Exception e) {
                AppView.notifyError("Erreur Import", "Le fichier est mal formé ou inaccessible.");
            }
        }
    }
}
// --- EXPORTS ---
// Méthodes pour exporter les données affichées dans la TableView vers différents formats (CSV
@FXML
private void handleExportCSV() {
    String path = AppView.showSaveDialog(studentTable, "csv");
    if (path != null) {
        exportService.exportToCSV(AppView.getTableItems(studentTable), path);
        AppView.notifySuccess("Fichier CSV exporté.");
    }
}
// Méthodes pour exporter les données affichées dans la TableView vers différents formats (CSV, JSON, XML, HTML)
@FXML
private void handleExportJSON() {
    String path = AppView.showSaveDialog(studentTable, "json");
    if (path != null) {
        exportService.exportToJSON(AppView.getTableItems(studentTable), path);
        AppView.notifySuccess("Fichier JSON exporté.");
    }
}
// Méthodes pour exporter les données affichées dans la TableView vers différents formats (CSV, JSON, XML, HTML)
@FXML
private void handleExportXML() {
    String path = AppView.showSaveDialog(studentTable, "xml");
    if (path != null) {
        exportService.exportToXML(AppView.getTableItems(studentTable), path);
        AppView.notifySuccess("Fichier XML exporté.");
    }
}
// Méthodes pour exporter les données affichées dans la TableView vers différents formats (CSV, JSON, XML, HTML)
@FXML
private void handleExportHTML() {
    String path = AppView.showSaveDialog(studentTable, "html");
    if (path != null) {
        // On récupère les statistiques actuelles pour le rapport
        String stats = studentDAO.getGlobalStats();
        exportService.exportToHTML(AppView.getTableItems(studentTable), stats, path);
        AppView.notifySuccess("Rapport HTML généré avec succès !");
    }
}
    // Méthode pour afficher les statistiques globales (total d'élèves, moyenne générale, répartition par âge) en utilisant une requête SQL GROUP BY dans le DAO.
    private void updateStatistics() {
        List<Student> currentList = AppView.getTableItems(studentTable);
        double sum = 0;
        for (Student s : currentList) sum += s.getGrade();
        double avg = currentList.isEmpty() ? 0 : sum / currentList.size();
        
        AppView.renderStats(statsCount, statsAverage, currentList.size(), avg);
    }
    // Méthode pour afficher les statistiques globales (total d'élèves, moyenne générale, répartition par âge) en utilisant une requête SQL GROUP BY dans le DAO.
    @FXML
    private void handleTableClick() {
            Student s = (Student) AppView.getSelectedItem(studentTable);
        if (s != null) { // Indispensable pour éviter des micro-erreurs au scroll
            AppView.fillForm(s, fNameInput, lNameInput, ageInput, gradeInput);
        }
    }
    // Méthode pour charger plus d'étudiants au scroll (pagination). Elle écoute les événements de scroll sur la TableView et charge la page suivante lorsque l'utilisateur atteint le bas du tableau.
    @FXML
    private void handleLogout(ActionEvent event) {
        UIUtils.switchScene(event, "WelcomeView.fxml", "LP Tracker - Connexion");
    }
    // Méthode pour réinitialiser tous les filtres et champs de recherche. Elle vide les champs texte, remet les ComboBox à leur valeur par défaut et rafraîchit la TableView pour afficher tous les étudiants.
    @FXML
    private void handleResetFilters() {
        // 1. Demander confirmation
        if (AppView.showConfirmation("Réinitialisation", "Voulez-vous vraiment effacer tous les filtres ?")) {
            
            // 2. Vider les champs texte (Filtres + Formulaire)
            AppView.clearFields(fNameInput, lNameInput, ageInput, gradeInput, searchFirstName, searchLastName, searchField);
            
            // 3. Remettre la ComboBox sur "Tous" via l'utilitaire (Règle ton erreur actuelle)
            AppView.resetComboBox(ageFilterCombo, "Tous");

            // 4. Rafraîchir les données
            refreshTable();
            
            AppView.notifySuccess("Interface réinitialisée.");
        }
    }
    // Méthode pour afficher les statistiques globales (total d'élèves, moyenne générale, répartition par âge) en utilisant une requête SQL GROUP BY dans le DAO.
    @FXML
    private void handleShowStatisticsChart() {
        // On récupère les étudiants actuellement visibles (filtrés ou non)
        List<Student> currentStudents = AppView.getTableItems(studentTable);
        
        if (currentStudents.isEmpty()) {
            AppView.notifyWarning("Données vides", "Aucun étudiant à analyser.");
            return;
        }

        // On appelle notre nouvelle classe
        StatChartView.display(currentStudents);
    }
}