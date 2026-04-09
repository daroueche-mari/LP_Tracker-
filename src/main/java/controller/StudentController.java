package controller;

import java.io.File;
import java.util.List;

import dao.StudentDAO;
import model.Student;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.util.Duration;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class StudentController {
    
    @FXML private TextField fNameInput;
    @FXML private TextField lNameInput;
    @FXML private TextField ageInput;
    @FXML private TextField gradeInput;
    @FXML private TextField searchIdField;
    @FXML private TableView<Student> studentTable;
    @FXML private TextField searchFirstNameField;
    @FXML private TextField searchLastNameField;
    @FXML private TableColumn<Student, String> colFirstName;
    @FXML private TableColumn<Student, String> colLastName;
    @FXML private TableColumn<Student, Integer> colAge;
    @FXML private TableColumn<Student, Double> colGrade;
    @FXML private Label pageLabel;
    @FXML private Label statsLabel;    
    @FXML private ComboBox<Integer> ageFilterCombo;
    private int currentPage = 0;
    private final int ROWS_PER_PAGE = 10;
    private StudentDAO dao = new StudentDAO();

    @FXML
    public void initialize() {
        // ... Garde ton code Timeline et cellValueFactory ...
        Timeline autoSave = new Timeline(new KeyFrame(Duration.minutes(5), event -> {
            dao.exportToCSV("autosave_students.csv");
            System.out.println("✅ Sauvegarde automatique effectuée.");
        }));
        autoSave.setCycleCount(Timeline.INDEFINITE);
        autoSave.play();

        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        if (ageFilterCombo != null) {
            for (int i = 18; i <= 60; i++) {
                ageFilterCombo.getItems().add(i);
            }
        }

        // 3. CHARGEMENT INITIAL AVEC PAGINATION (La correction est ici)
        currentPage = 0;      // On force le départ à la première page
        updatePagedTable();   // On appelle la pagination au lieu de refreshTable()
}
    @FXML
    private void handleNextPage() {
        currentPage++;
        updatePagedTable();
    }

    @FXML
    private void handlePrevPage() {
        if (currentPage > 0) {
            currentPage--;
            updatePagedTable();
        }
    }

    private void updatePagedTable() {
    // 1. Calcul de l'index de départ
    int offset = currentPage * ROWS_PER_PAGE;
    List<Student> pagedList = dao.getStudentsPaged(ROWS_PER_PAGE, offset);
    
    // 2. Sécurité : si on arrive sur une page vide (ex: après une suppression)
    if (pagedList.isEmpty() && currentPage > 0) {
        currentPage--;
        updatePagedTable(); // On relance pour afficher la page précédente
        return;
    }
    
    // 3. Mise à jour de la TableView
    studentTable.setItems(FXCollections.observableArrayList(pagedList));
    
    // 4. MISE À JOUR DU LABEL (Indispensable pour le jury !)
    if (pageLabel != null) {
        pageLabel.setText("Page " + (currentPage + 1));
    }

    // 5. Mise à jour des stats (Optionnel mais recommandé)
    // Cela permet de voir les stats globales même en changeant de page
    statsLabel.setText(dao.getGlobalStats());
}
    /**
     * Remplit les champs quand on clique sur une ligne du tableau
     */
    @FXML
    private void handleTableClick() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            fNameInput.setText(selected.getFirstName());
            lNameInput.setText(selected.getLastName());
            ageInput.setText(String.valueOf(selected.getAge()));
            gradeInput.setText(String.valueOf(selected.getGrade()));
        }
    }

    /**
     * Action : AJOUTER
     */
    @FXML
    private void handleAddStudent() {
        boolean success = addStudent(
            fNameInput.getText(), lNameInput.getText(), 
            ageInput.getText(), gradeInput.getText()
        );
        if (success) {
            refreshTable();
            clearFields();
        }
    }

    /**
     * Action : SUPPRIMER
     */
    @FXML
    private void handleDeleteStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dao.deleteStudent(selected.getId()); // Vérifie que deleteStudent(int id) existe dans ton DAO
            refreshTable();
            clearFields();
            System.out.println("✅ Étudiant supprimé !");
        } else {
            System.out.println("⚠️ Sélectionnez d'abord un étudiant.");
        }
    }

    /**
     * Action : MODIFIER
     */
    @FXML
    private void handleUpdateStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                // Mise à jour de l'objet avec les nouvelles saisies
                selected.setFirstName(fNameInput.getText());
                selected.setLastName(lNameInput.getText());
                selected.setAge(Integer.parseInt(ageInput.getText()));
                selected.setGrade(Double.parseDouble(gradeInput.getText()));

                dao.updateStudent(selected); // Vérifie que updateStudent(Student s) existe dans ton DAO
                refreshTable();
                clearFields();
                System.out.println("✅ Étudiant mis à jour !");
            } catch (NumberFormatException e) {
                System.err.println("❌ Erreur : Format nombre invalide.");
            }
        }
    }
    @FXML private TextField searchField; // Assure-toi que le fx:id correspond dans le FXML

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        
        if (keyword.trim().isEmpty()) {
            refreshTable(); // Si le champ est vide, on réaffiche tout
        } else {
            // On remplace le contenu du tableau par le résultat du filtre
            ObservableList<Student> filteredList = FXCollections.observableArrayList(dao.searchStudents(keyword));
            studentTable.setItems(filteredList);
        }
    }
    @FXML
    private void handleSearchByID() {
        try {
            int id = Integer.parseInt(searchIdField.getText()); // Récupère l'ID du TextField
            Student s = dao.getStudentById(id);
            
            if (s != null) {
                studentTable.setItems(FXCollections.observableArrayList(s));
                updateStatsForSelection(List.of(s));
            } else {
                showAlert("Résultat", "Aucun étudiant trouvé avec l'ID : " + id);
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer un ID numérique valide.");
        }
    }
    private void updateStatsForSelection(List<Student> students) {
    if (students == null || students.isEmpty()) {
        statsLabel.setText("Aucun résultat trouvé pour ces critères.");
        return;
    }
    
    double sum = 0;
    for (Student s : students) {
        sum += s.getGrade();
    }
    double avg = sum / students.size();
    
    // On affiche le nombre de résultats trouvés et leur moyenne spécifique
    statsLabel.setText(String.format("Résultats : %d élèves | Moyenne du groupe : %.2f/20", 
                       students.size(), avg));
}
    @FXML
    private void handleResetFilters() {
    // 1. Vider les champs de texte
    searchFirstNameField.clear();
    searchLastNameField.clear();
    
    // 2. Réinitialiser la ComboBox proprement
    if (ageFilterCombo != null) {
        ageFilterCombo.getSelectionModel().clearSelection();
        ageFilterCombo.setPromptText("Âge");
    }
    
    // 3. Réinitialiser la pagination
    currentPage = 0; // TRÈS IMPORTANT : remettre à zéro AVANT d'appeler la méthode
    updatePagedTable(); // Cette méthode s'occupe déjà de rafraîchir la table avec les 10 premiers
    
    // 4. Facultatif : Remettre le curseur dans le premier champ
    searchFirstNameField.requestFocus();
    
    System.out.println("🔄 Filtres réinitialisés et retour à la page 1.");
}
    @FXML
    private void handleExportCSV() {
        dao.exportToCSV("liste_etudiants.csv");
        System.out.println("✅ Exportation réalisée !");
    }

    @FXML
    private void handleLogout() {
    try {
        // Chargement de la vue de connexion
        Parent loginView = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
        Stage stage = (Stage) studentTable.getScene().getWindow();
        
        // On change la scène
        stage.setScene(new Scene(loginView, 400, 300));
        stage.setTitle("Authentification - LP Tracker");
        stage.centerOnScreen();
        
        System.out.println("👋 Déconnecté avec succès.");
    } catch (Exception e) {
        e.printStackTrace();
    }
}
@FXML
private void handleImportCSV() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choisir le fichier CSV à importer");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
    File file = fileChooser.showOpenDialog(studentTable.getScene().getWindow());

    if (file != null) {
        dao.importFromCSV(file.getAbsolutePath());
        refreshTable(); // On recharge les données pour voir les nouveaux élèves
    }
}
@FXML
private void handleExportHTML() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Enregistrer le rapport HTML");
    fileChooser.setInitialFileName("rapport_resultats.html");
    File file = fileChooser.showSaveDialog(studentTable.getScene().getWindow());

    if (file != null) {
        dao.exportResultsToHTML(file.getAbsolutePath());
    }
}

    // --- Utilitaires ---

    public void refreshTable() {
        if (studentTable != null) {
            studentTable.setItems(getStudentsList());
            statsLabel.setText(dao.getGlobalStats()); // Mise à jour automatique
        }
    }

    public ObservableList<Student> getStudentsList() {
        return FXCollections.observableArrayList(dao.getAllStudents());
    }

    private void clearFields() {
        fNameInput.clear();
        lNameInput.clear();
        ageInput.clear();
        gradeInput.clear();
    }

    public boolean addStudent(String fName, String lName, String ageStr, String gradeStr) {
        try {
            int age = Integer.parseInt(ageStr);
            double grade = Double.parseDouble(gradeStr);
            Student s = new Student(fName, lName, age, grade);
            dao.addStudent(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    @FXML
    private void handleAdvancedSearch() {
        // On récupère les textes des deux champs distincts
        String firstName = searchFirstNameField.getText(); // Assure-toi d'avoir ce fx:id
        String lastName = searchLastNameField.getText();   // Et celui-ci
        
        Integer age = (ageFilterCombo != null) ? ageFilterCombo.getValue() : null;

        // Appel au DAO avec les nouveaux critères
        List<Student> results = dao.findAdvanced(firstName, lastName, age);
        
        studentTable.setItems(FXCollections.observableArrayList(results));
        updateStatsForSelection(results);
    }
    private void showAlert(String title, String content) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
}
}