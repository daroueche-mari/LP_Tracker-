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
import model.Student;
import util.UIUtils;
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
    
    // --- Variables de pagination ---
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

        // 3. Charger la première page
        refreshTable();

        // 4. Filtrage dynamique (Égalité exacte comme demandé)
        FilteredList<Student> filteredData = new FilteredList<>(masterData, p -> true);
        Runnable updateFilter = () -> {
            filteredData.setPredicate(student -> {
                String fNameFilter = searchFirstName.getText().toLowerCase().trim();
                String lNameFilter = searchLastName.getText().toLowerCase().trim();
                String ageFilter = ageFilterCombo.getValue();

                if (!fNameFilter.isEmpty() && !student.getFirstName().toLowerCase().equals(fNameFilter)) return false;
                if (!lNameFilter.isEmpty() && !student.getLastName().toLowerCase().equals(lNameFilter)) return false;
                if (ageFilter != null && !ageFilter.equals("Tous")) {
                    if (student.getAge() != Integer.parseInt(ageFilter)) return false;
                }
                return true;
            });
        };

        searchFirstName.textProperty().addListener((obs, old, val) -> updateFilter.run());
        searchLastName.textProperty().addListener((obs, old, val) -> updateFilter.run());
        ageFilterCombo.valueProperty().addListener((obs, old, val) -> updateFilter.run());

        // 5. Liaison TableView
        SortedList<Student> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(studentTable.comparatorProperty());
        studentTable.setItems(sortedData);

        // 6. Activer le Scroll Infini
        Platform.runLater(this::setupInfiniteScrolling);
    }

    // --- LOGIQUE DE PAGINATION ---

    private void setupInfiniteScrolling() {
        for (Node node : studentTable.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar) {
                ScrollBar scrollBar = (ScrollBar) node;
                if (scrollBar.getOrientation() == Orientation.VERTICAL) {
                    scrollBar.valueProperty().addListener((obs, oldVal, newVal) -> {
                        double max = scrollBar.getMax();
                        // Si l'utilisateur a scrollé à plus de 90% du bas
                        if (newVal.doubleValue() > (max * 0.9) && !isLoading) {
                            loadNextPage();
                        }
                    });
                }
            }
        }
    }

    private void loadNextPage() {
        isLoading = true;
        currentPage++;
        // On demande au DAO les étudiants suivants (Offset)
        List<Student> nextStudents = studentDAO.getStudentsPaged(currentPage, ROWS_PER_PAGE);
        if (nextStudents != null && !nextStudents.isEmpty()) {
            masterData.addAll(nextStudents);
        }
        isLoading = false;
    }

    private void refreshTable() {
        currentPage = 0;
        // On remplace tout par la page 0
        masterData.setAll(studentDAO.getStudentsPaged(currentPage, ROWS_PER_PAGE));
    }

    // --- ACTIONS BOUTONS ---

    @FXML
    private void handleResetFilters() {
        searchFirstName.clear();
        searchLastName.clear();
        ageFilterCombo.setValue("Tous");
        fNameInput.clear();
        lNameInput.clear();
        ageInput.clear();
        gradeInput.clear();
        studentTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleTableClick(MouseEvent event) {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            fNameInput.setText(selected.getFirstName());
            lNameInput.setText(selected.getLastName());
            ageInput.setText(String.valueOf(selected.getAge()));
            gradeInput.setText(String.valueOf(selected.getGrade()));
        }
    }

    @FXML private void handleAddStudent() { refreshTable(); handleResetFilters(); }
    @FXML private void handleUpdateStudent() { refreshTable(); handleResetFilters(); }
    @FXML private void handleDeleteStudent() { refreshTable(); handleResetFilters(); }

    @FXML
    private void handleLogout(ActionEvent event) {
        UIUtils.switchScene(event, "/view/LoginView.fxml", "Connexion");
    }

    // --- HOVERS ET AUTRES ---
    @FXML private void handleExportCSV() { System.out.println("Export CSV..."); }
    @FXML private void handleImportCSV() { System.out.println("Import CSV..."); }
    @FXML private void handleExportHTML() { System.out.println("Rapport HTML..."); }
    @FXML private void handleHoverEnterSide(MouseEvent e) {}
    @FXML private void handleHoverExitSide(MouseEvent e) {}
    @FXML private void handleHoverEnterLogout(MouseEvent e) {}
    @FXML private void handleHoverExitLogout(MouseEvent e) {}
    @FXML private void handleHoverEnterRed(MouseEvent e) {}
    @FXML private void handleHoverExitRed(MouseEvent e) {}
    @FXML private void handleHoverEnterBlue(MouseEvent e) {}
    @FXML private void handleHoverExitBlue(MouseEvent e) {}
    @FXML private void handleHoverEnterGreen(MouseEvent e) {}
    @FXML private void handleHoverExitGreen(MouseEvent e) {}
}