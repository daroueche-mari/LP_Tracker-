package controller;

import dao.StudentDAO;
import model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

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
    
    private StudentDAO dao = new StudentDAO();

    @FXML
    public void initialize() {
        // Liaison colonnes <-> attributs du modèle Student
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

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
}