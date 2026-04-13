package controller;

import dao.StudentDAO;
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
import util.UIUtils;

import java.io.*;
import java.util.List;

public class StudentController {

    @FXML private TextField fNameInput, lNameInput, ageInput, gradeInput;
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> colFirstName, colLastName;
    @FXML private TableColumn<Student, Integer> colAge;
    @FXML private TableColumn<Student, Double> colGrade;
    @FXML private ComboBox<String> ageFilterCombo;
    @FXML private TextField searchFirstName, searchLastName;

    private StudentDAO studentDAO = new StudentDAO();
    private ObservableList<Student> masterData = FXCollections.observableArrayList();
    
    private int currentPage = 0;
    private final int ROWS_PER_PAGE = 20;
    private boolean isLoading = false;

    @FXML
    public void initialize() {
        // 1. Liaison des colonnes
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        // 2. ComboBox Âges
        ObservableList<String> ages = FXCollections.observableArrayList("Tous");
        for (int i = 18; i <= 70; i++) { ages.add(String.valueOf(i)); }
        ageFilterCombo.setItems(ages);
        ageFilterCombo.setValue("Tous");

        // 3. Charger les données initiales
        refreshTable();

        // 4. Filtrage dynamique
        FilteredList<Student> filteredData = new FilteredList<>(masterData, p -> true);
        Runnable updateFilter = () -> {
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
        };

        searchFirstName.textProperty().addListener((obs, old, val) -> updateFilter.run());
        searchLastName.textProperty().addListener((obs, old, val) -> updateFilter.run());
        ageFilterCombo.valueProperty().addListener((obs, old, val) -> updateFilter.run());

        // 5. Liaison TableView avec Tri
        SortedList<Student> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(studentTable.comparatorProperty());
        studentTable.setItems(sortedData);

        // 6. Scroll Infini
        Platform.runLater(this::setupInfiniteScrolling);
    }

    // --- LOGIQUE DE DONNÉES ---

    private void refreshTable() {
        currentPage = 0;
        List<Student> data = studentDAO.getStudentsPaged(currentPage, ROWS_PER_PAGE);
        masterData.setAll(data);
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
        if (nextStudents != null && !nextStudents.isEmpty()) {
            masterData.addAll(nextStudents);
        }
        isLoading = false;
    }

    // --- ACTIONS CRUD ---

    @FXML
    private void handleAddStudent() {
        try {
            String fName = fNameInput.getText().trim();
            String lName = lNameInput.getText().trim();
            int age = Integer.parseInt(ageInput.getText());
            double grade = Double.parseDouble(gradeInput.getText());

            if (fName.isEmpty() || lName.isEmpty()) throw new Exception();

            studentDAO.addStudent(new Student(0, fName, lName, age, grade));
            showNotification("Succès", "Étudiant ajouté !");
            refreshTable();
            handleResetFilters();
        } catch (Exception e) {
            showError("Erreur", "Vérifiez les champs (Âge: entier, Note: 0-20).");
        }
    }

    @FXML
    private void handleUpdateStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Sélection", "Veuillez sélectionner un étudiant.");
            return;
        }
        try {
            selected.setFirstName(fNameInput.getText());
            selected.setLastName(lNameInput.getText());
            selected.setAge(Integer.parseInt(ageInput.getText()));
            selected.setGrade(Double.parseDouble(gradeInput.getText()));

            studentDAO.updateStudent(selected);
            showNotification("Succès", "Données mises à jour.");
            refreshTable();
        } catch (Exception e) { showError("Erreur", "Données invalides."); }
    }

    @FXML
    private void handleDeleteStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            studentDAO.deleteStudent(selected.getId());
            showNotification("Suppression", "Étudiant retiré.");
            refreshTable();
            handleResetFilters();
        }
    }

    // --- IMPORT / EXPORT (CSV, JSON, XML, PDF/HTML) ---

    @FXML
    private void handleExportCSV() {
        File file = getSaveLocation("export.csv", "CSV", "*.csv");
        if (file == null) return;
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.println("Prenom;Nom;Age;Note");
            for (Student s : studentTable.getItems()) {
                pw.println(s.getFirstName() + ";" + s.getLastName() + ";" + s.getAge() + ";" + s.getGrade());
            }
            showNotification("Succès", "Export CSV réussi.");
        } catch (Exception e) { showError("Erreur", "Export CSV échoué."); }
    }

    @FXML
    private void handleImportCSV() {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(studentTable.getScene().getWindow());
        if (file == null) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line; br.readLine(); // Header
            while ((line = br.readLine()) != null) {
                String[] v = line.split(";");
                studentDAO.addStudent(new Student(0, v[0], v[1], Integer.parseInt(v[2]), Double.parseDouble(v[3])));
            }
            refreshTable();
            showNotification("Succès", "Importation terminée.");
        } catch (Exception e) { showError("Erreur", "Fichier CSV corrompu."); }
    }

    @FXML
    private void handleExportJSON() {
        File file = getSaveLocation("etudiants.json", "JSON", "*.json");
        if (file == null) return;
        StringBuilder sb = new StringBuilder("[\n");
        List<Student> list = studentTable.getItems();
        for (int i = 0; i < list.size(); i++) {
            Student s = list.get(i);
            sb.append(String.format("  {\"prenom\":\"%s\", \"nom\":\"%s\", \"age\":%d, \"note\":%.2f}", 
                      s.getFirstName(), s.getLastName(), s.getAge(), s.getGrade()));
            if (i < list.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        saveStringToFile(sb.toString(), file);
    }

    @FXML
    private void handleExportXML() {
        File file = getSaveLocation("etudiants.xml", "XML", "*.xml");
        if (file == null) return;
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<students>\n");
        for (Student s : studentTable.getItems()) {
            sb.append("  <student>\n")
              .append("    <firstname>").append(s.getFirstName()).append("</firstname>\n")
              .append("    <lastname>").append(s.getLastName()).append("</lastname>\n")
              .append("    <age>").append(s.getAge()).append("</age>\n")
              .append("    <grade>").append(s.getGrade()).append("</grade>\n")
              .append("  </student>\n");
        }
        sb.append("</students>");
        saveStringToFile(sb.toString(), file);
    }

    @FXML
    private void handleExportHTML() {
        File file = new File("Rapport_Promotion.html");
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.println("<html><head><style>table{width:100%; border-collapse:collapse;} th,td{border:1px solid #ddd; padding:8px;} th{background:#3d5afe; color:white;}</style></head><body>");
            pw.println("<h1>Rapport Etudiants</h1>");
            pw.println("<p>" + studentDAO.getGlobalStats().replace("\n", "<br>") + "</p>");
            pw.println("<table><tr><th>Prénom</th><th>Nom</th><th>Age</th><th>Note</th></tr>");
            for (Student s : studentTable.getItems()) {
                pw.println(String.format("<tr><td>%s</td><td>%s</td><td>%d</td><td>%.2f</td></tr>", 
                           s.getFirstName(), s.getLastName(), s.getAge(), s.getGrade()));
            }
            pw.println("</table></body></html>");
            if (java.awt.Desktop.isDesktopSupported()) java.awt.Desktop.getDesktop().browse(file.toURI());
            showNotification("Rapport", "HTML généré et ouvert.");
        } catch (Exception e) { showError("Erreur", "Rapport impossible."); }
    }

    // --- UTILITAIRES ---

    private void saveStringToFile(String content, File file) {
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.write(content);
            showNotification("Succès", "Fichier sauvegardé.");
        } catch (Exception e) { showError("Erreur", "Écriture impossible."); }
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
        a.setTitle(title); a.setContentText(content); a.showAndWait();
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
}