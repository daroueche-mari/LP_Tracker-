package controller;

import java.io.File;
import java.util.List;

import dao.StudentDAO;
import model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
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

    @FXML private TableView<Student> studentTable;

    @FXML private TableColumn<Student, String> colFirstName;
    @FXML private TableColumn<Student, String> colLastName;
    @FXML private TableColumn<Student, Integer> colAge;
    @FXML private TableColumn<Student, Double> colGrade;

    @FXML private Label statsLabel;

    @FXML private TextField searchNameField;
    @FXML private TextField minGradeField;
    
    @FXML private ComboBox<Integer> ageFilterCombo;

    private StudentDAO dao = new StudentDAO();

    @FXML
    public void initialize() {
        // 1. Liaison colonnes <-> attributs du modèle Student
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        // 2. Remplissage du ComboBox pour l'âge (ex: de 18 à 60 ans)
        // Cela évite l'erreur "cannot be resolved" et permet de choisir un âge
        if (ageFilterCombo != null) {
            for (int i = 18; i <= 60; i++) {
                ageFilterCombo.getItems().add(i);
            }
        }

        // 3. Chargement initial des données
        refreshTable();
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
    private void resetFilters() {
        searchNameField.clear();
        minGradeField.clear();
        if (ageFilterCombo != null) ageFilterCombo.getSelectionModel().clearSelection();
        
        // On réaffiche tout le monde
        refreshTable(); 
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
        String name = searchNameField.getText();
        
        Double minGrade = null;
        try {
            if (!minGradeField.getText().isEmpty()) 
                minGrade = Double.parseDouble(minGradeField.getText().replace(",", "."));
        } catch (NumberFormatException e) { /* Ignorer si mal tapé */ }

        Integer age = ageFilterCombo.getValue(); // Si tu as rempli le combo

        List<Student> results = dao.findAdvanced(name, minGrade, age);
        studentTable.setItems(FXCollections.observableArrayList(results));
        
        // On met à jour les stats pour ne voir que les stats des résultats filtrés !
        updateStatsForSelection(results); 
    }
}