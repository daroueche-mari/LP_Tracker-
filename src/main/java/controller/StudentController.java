package controller;

import dao.StudentDAO;
import exception.ValidationException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import model.Student;
import service.ExportService;
import service.StudentService;
import util.UIUtils;

import java.io.File;
import java.util.List;

public class StudentController {
    // --- UI Components ---
    @FXML private TextField fNameInput, lNameInput, ageInput, gradeInput; 
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> colFirstName, colLastName;
    @FXML private TableColumn<Student, Integer> colAge;
    @FXML private TableColumn<Student, Double> colGrade;
    @FXML private ComboBox<String> ageFilterCombo;
    @FXML private TextField searchFirstName, searchLastName;
    @FXML private Label statsLabel; // Optionnel : pour afficher la moyenne en direct

    // --- Services ---
    private final StudentDAO studentDAO = new StudentDAO();
    private final ExportService exportService = new ExportService();
    private final StudentService studentService = new StudentService();
    private final ObservableList<Student> masterData = FXCollections.observableArrayList();

    private int currentPage = 0;
    private final int ROWS_PER_PAGE = 20;
    private boolean isLoading = false;

    @FXML
    public void initialize() {
        // 1. Liaison colonnes
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        // 2. ComboBox Âge
        ObservableList<String> ages = FXCollections.observableArrayList("Tous");
        for (int i = 18; i <= 70; i++) { ages.add(String.valueOf(i)); }
        ageFilterCombo.setItems(ages);
        ageFilterCombo.setValue("Tous");

        // 3. Données et Filtres
        refreshTable();
        FilteredList<Student> filteredData = new FilteredList<>(masterData, p -> true);
        
        searchFirstName.textProperty().addListener((obs, old, val) -> applyFilters(filteredData));
        searchLastName.textProperty().addListener((obs, old, val) -> applyFilters(filteredData));
        ageFilterCombo.valueProperty().addListener((obs, old, val) -> applyFilters(filteredData));

        SortedList<Student> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(studentTable.comparatorProperty());
        studentTable.setItems(sortedData);

        Platform.runLater(this::setupInfiniteScrolling);
    }

    private void applyFilters(FilteredList<Student> filteredData) {
        filteredData.setPredicate(student -> {
            String fNameFilter = searchFirstName.getText().toLowerCase().trim();
            String lNameFilter = searchLastName.getText().toLowerCase().trim();
            String ageFilter = ageFilterCombo.getValue();

            if (!fNameFilter.isEmpty() && !student.getFirstName().toLowerCase().contains(fNameFilter)) return false;
            if (!lNameFilter.isEmpty() && !student.getLastName().toLowerCase().contains(lNameFilter)) return false;
            if (ageFilter != null && !ageFilter.equals("Tous")) {
                if (student.getAge() != Integer.parseInt(ageFilter)) return false;
            }
            return true;
        });
        updateLiveStats(); // Mise à jour auto de la moyenne quand on filtre
    }

    private void refreshTable() {
        currentPage = 0;
        masterData.setAll(studentDAO.getStudentsPaged(currentPage, ROWS_PER_PAGE));
        updateLiveStats();
    }

    // --- ACTIONS CRUD (Nettoyées grâce au Service) ---

    @FXML
    private void handleAddStudent() {
        try {
            studentService.validateAndAdd(
                fNameInput.getText(), lNameInput.getText(), 
                ageInput.getText(), gradeInput.getText()
            );
            showNotification("Succès", "Étudiant ajouté avec succès !");
            refreshTable();
            handleResetFilters();
        } catch (ValidationException e) {
            showError("Erreur de saisie", e.getMessage());
        }
    }

    @FXML
    private void handleUpdateStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Sélection", "Veuillez sélectionner un étudiant dans la liste.");
            return;
        }
        try {
            studentService.validateAndUpdate(
                selected, 
                fNameInput.getText(), lNameInput.getText(), 
                ageInput.getText(), gradeInput.getText()
            );
            showNotification("Succès", "Données mises à jour.");
            refreshTable();
        } catch (ValidationException e) {
            showError("Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            studentService.deleteStudent(selected.getId());
            showNotification("Suppression", "Étudiant retiré.");
            refreshTable();
        }
    }

    // --- EXPORTS & STATS ---

    private void updateLiveStats() {
        if (statsLabel != null) {
            statsLabel.setText(studentService.getFormattedStatsForSelection(studentTable.getItems()));
        }
    }

    @FXML
    private void handleExportHTML() {
        File file = new File("Rapport_Promotion.html");
        String stats = studentService.getGlobalStatsString();
        exportService.exportToHTML(studentTable.getItems(), stats, file.getAbsolutePath());
        
        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().browse(file.toURI());
            }
        } catch (Exception e) { showError("Erreur", "Impossible d'ouvrir le rapport."); }
    }

    // --- LOGIQUE SCROLL & UI UTILS (Inchangé) ---

    @FXML
    private void handleImportCSV() {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(studentTable.getScene().getWindow());
        if (file != null) {
            exportService.importFromCSV(file.getAbsolutePath(), studentDAO);
            refreshTable();
        }
    }

    @FXML
    private void handleExportCSV() { exportToFile("export.csv", "CSV", "*.csv", "csv"); }
    @FXML
    private void handleExportJSON() { exportToFile("etudiants.json", "JSON", "*.json", "json"); }
    @FXML
    private void handleExportXML() { exportToFile("etudiants.xml", "XML", "*.xml", "xml"); }

    private void exportToFile(String defName, String desc, String ext, String type) {
        File file = getSaveLocation(defName, desc, ext);
        if (file != null) {
            switch(type) {
                case "csv" -> exportService.exportToCSV(studentTable.getItems(), file.getAbsolutePath());
                case "json" -> exportService.exportToJSON(studentTable.getItems(), file.getAbsolutePath());
                case "xml" -> exportService.exportToXML(studentTable.getItems(), file.getAbsolutePath());
            }
            showNotification("Export", "Fichier " + type.toUpperCase() + " généré !");
        }
    }

    private File getSaveLocation(String defName, String desc, String ext) {
        FileChooser fc = new FileChooser();
        fc.setInitialFileName(defName);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(desc, ext));
        return fc.showSaveDialog(studentTable.getScene().getWindow());
    }

    private void showNotification(String title, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(content); a.showAndWait();
    }

    private void showError(String title, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(content); a.showAndWait();
    }

    @FXML
    private void handleResetFilters() {
        searchFirstName.clear(); searchLastName.clear();
        fNameInput.clear(); lNameInput.clear();
        ageInput.clear(); gradeInput.clear();
        ageFilterCombo.setValue("Tous");
        studentTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        UIUtils.switchScene(event, "WelcomeView.fxml", "LP Tracker - Connexion");
    }

    @FXML
    private void handleTableClick(MouseEvent event) {
        Student s = studentTable.getSelectionModel().getSelectedItem();
        if (s != null) {
            fNameInput.setText(s.getFirstName());
            lNameInput.setText(s.getLastName());
            ageInput.setText(String.valueOf(s.getAge()));
            gradeInput.setText(String.valueOf(s.getGrade()));
        }
    }

    private void setupInfiniteScrolling() {
        for (Node node : studentTable.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar scrollBar && scrollBar.getOrientation() == Orientation.VERTICAL) {
                scrollBar.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal.doubleValue() > (scrollBar.getMax() * 0.9) && !isLoading) {
                        loadNextPage();
                    }
                });
            }
        }
    }

    private void loadNextPage() {
        isLoading = true;
        currentPage++;
        List<Student> nextStudents = studentDAO.getStudentsPaged(currentPage, ROWS_PER_PAGE);
        if (nextStudents != null && !nextStudents.isEmpty()) masterData.addAll(nextStudents);
        isLoading = false;
    }
}