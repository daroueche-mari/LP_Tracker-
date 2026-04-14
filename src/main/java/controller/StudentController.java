package controller;
// Importations des classes nécessaires pour le fonctionnement du contrôleur (ex: pour gérer les événements, manipuler les composants de l'interface utilisateur, interagir avec les services et les DAO, etc.)
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
    // --- Pagination & Infinite Scroll ---
    private int currentPage = 0;
    private final int ROWS_PER_PAGE = 20;
    private boolean isLoading = false;
    
    @FXML
    public void initialize() { // Méthode d'initialisation appelée automatiquement par JavaFX après le chargement du FXML (ex: pour configurer les colonnes de la TableView, initialiser les filtres, charger les données initiales, etc.)
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
        // 4. Listeners pour les champs de recherche et le ComboBox de filtre d'âge
        searchFirstName.textProperty().addListener((obs, old, val) -> applyFilters(filteredData));
        searchLastName.textProperty().addListener((obs, old, val) -> applyFilters(filteredData));
        ageFilterCombo.valueProperty().addListener((obs, old, val) -> applyFilters(filteredData));
        // 5. Tri et liaison avec la TableView
        SortedList<Student> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(studentTable.comparatorProperty());
        studentTable.setItems(sortedData);
        // 6. Setup de l'infinite scrolling (doit être fait après que la TableView soit complètement initialisée)
        Platform.runLater(this::setupInfiniteScrolling);
    }
    // Méthode pour appliquer les filtres de recherche et d'âge à la liste des étudiants affichée dans la TableView (ex: lorsque l'utilisateur tape dans les champs de recherche ou change le filtre d'âge, la liste se met à jour automatiquement pour ne montrer que les étudiants correspondants)
    private void applyFilters(FilteredList<Student> filteredData) {
        filteredData.setPredicate(student -> {
            String fNameFilter = searchFirstName.getText().toLowerCase().trim();
            String lNameFilter = searchLastName.getText().toLowerCase().trim();
            String ageFilter = ageFilterCombo.getValue();
            // Vérification des critères de filtrage
            if (!fNameFilter.isEmpty() && !student.getFirstName().toLowerCase().contains(fNameFilter)) return false;
            if (!lNameFilter.isEmpty() && !student.getLastName().toLowerCase().contains(lNameFilter)) return false;
            if (ageFilter != null && !ageFilter.equals("Tous")) {
                if (student.getAge() != Integer.parseInt(ageFilter)) return false;
            }
            return true;
        });
        updateLiveStats(); // Mise à jour auto de la moyenne quand on filtre
    }
    // Méthode pour rafraîchir la TableView avec les données de la base de données, en réinitialisant la pagination et en mettant à jour les statistiques affichées (ex: après une opération de création, mise à jour ou suppression d'un étudiant, ou après l'importation de données)
    private void refreshTable() {
        currentPage = 0;
        masterData.setAll(studentDAO.getStudentsPaged(currentPage, ROWS_PER_PAGE));
        updateLiveStats();
    }

    // --- ACTIONS CRUD (Nettoyées grâce au Service) ---
    // Les méthodes handleAddStudent, handleUpdateStudent et handleDeleteStudent utilisent désormais le StudentService pour la validation et la logique métier, ce qui permet de garder le Controller plus propre et de centraliser la logique de gestion des étudiants dans le Service (ex: pour faciliter la maintenance et les évolutions futures de l'application)
    @FXML
    private void handleAddStudent() {
        try { // Appel à la méthode de validation et d'ajout du Service, qui peut lancer une ValidationException en cas de problème avec les données saisies
            studentService.validateAndAdd(
                fNameInput.getText(), lNameInput.getText(), 
                ageInput.getText(), gradeInput.getText()
            ); // Appel à la méthode de validation et d'ajout du Service, qui peut lancer une ValidationException en cas de problème avec les données saisies
            showNotification("Succès", "Étudiant ajouté avec succès !");
            refreshTable();
            handleResetFilters();
        } catch (ValidationException e) { // En cas de ValidationException, on affiche une alerte d'erreur avec le message de l'exception (ex: si l'utilisateur a saisi une note invalide ou un âge hors des limites, il recevra un message d'erreur clair indiquant le problème)
            showError("Erreur de saisie", e.getMessage());
        }
    }
    // Méthode pour gérer la mise à jour d'un étudiant sélectionné dans la TableView, en utilisant le Service pour la validation et la logique métier (ex: lorsque l'utilisateur modifie les détails d'un étudiant et clique sur "Enregistrer", cette méthode est appelée pour valider les données et mettre à jour l'étudiant dans la base de données)
    @FXML
    private void handleUpdateStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) { // Si aucun étudiant n'est sélectionné, on affiche une alerte d'erreur pour informer l'utilisateur qu'il doit sélectionner un étudiant avant de pouvoir le modifier (ex: pour éviter les erreurs de nullité et guider l'utilisateur dans l'utilisation de l'interface)
            showError("Sélection", "Veuillez sélectionner un étudiant dans la liste.");
            return;
        }
        try { // Appel à la méthode de validation et de mise à jour du Service, qui peut lancer une ValidationException en cas de problème avec les données saisies (ex: si l'utilisateur a saisi une note invalide ou un âge hors des limites, il recevra un message d'erreur clair indiquant le problème)
            studentService.validateAndUpdate(
                selected, 
                fNameInput.getText(), lNameInput.getText(), 
                ageInput.getText(), gradeInput.getText()
            ); // Appel à la méthode de validation et de mise à jour du Service, qui peut lancer une ValidationException en cas de problème avec les données saisies (ex: si l'utilisateur a saisi une note invalide ou un âge hors des limites, il recevra un message d'erreur clair indiquant le problème)
            showNotification("Succès", "Données mises à jour.");
            refreshTable();
        } catch (ValidationException e) { // En cas de ValidationException, on affiche une alerte d'erreur avec le message de l'exception (ex: si l'utilisateur a saisi une note invalide ou un âge hors des limites, il recevra un message d'erreur clair indiquant le problème)
            showError("Erreur", e.getMessage());
        }
    }
    // Méthode pour gérer la suppression d'un étudiant sélectionné dans la TableView, en utilisant le Service pour effectuer la suppression (ex: lorsque l'utilisateur clique sur le bouton "Supprimer" dans les détails d'un étudiant, cette méthode est appelée pour supprimer l'étudiant de la base de données)
    @FXML
    private void handleDeleteStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) { // Si un étudiant est sélectionné, on appelle la méthode de suppression du Service en passant l'ID de l'étudiant (ex: pour supprimer l'étudiant de la base de données)
            studentService.deleteStudent(selected.getId());
            showNotification("Suppression", "Étudiant retiré.");
            refreshTable();
        }
    }

    // --- EXPORTS & STATS ---
    // Méthode pour mettre à jour les statistiques affichées en fonction de la sélection actuelle dans la TableView (ex: pour afficher la moyenne des notes des étudiants actuellement affichés, ou d'autres statistiques pertinentes, afin de donner à l'utilisateur une vue d'ensemble de la promotion)
    private void updateLiveStats() {
        if (statsLabel != null) {
            statsLabel.setText(studentService.getFormattedStatsForSelection(studentTable.getItems()));
        }
    }
    // Méthode pour gérer l'exportation de la liste des étudiants et des statistiques dans un fichier HTML, en utilisant le Service d'exportation pour générer le fichier (ex: lorsque l'utilisateur clique sur "Exporter en HTML", cette méthode est appelée pour créer un rapport visuel de la promotion à partager ou à imprimer)
    @FXML
    private void handleExportHTML() {
        File file = new File("Rapport_Promotion.html");
        String stats = studentService.getGlobalStatsString();
        exportService.exportToHTML(studentTable.getItems(), stats, file.getAbsolutePath());
        // Après l'exportation, on tente d'ouvrir automatiquement le fichier HTML généré dans le navigateur par défaut de l'utilisateur (ex: pour offrir une expérience utilisateur fluide en permettant de visualiser immédiatement le rapport généré)
        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().browse(file.toURI());
            }
        } catch (Exception e) { showError("Erreur", "Impossible d'ouvrir le rapport."); } 
    }

    // --- LOGIQUE SCROLL & UI UTILS (Inchangé) ---
    // Méthode pour gérer l'importation d'étudiants depuis un fichier CSV, en utilisant le Service d'exportation pour lire le fichier et ajouter les étudiants à la base de données (ex: lorsque l'utilisateur clique sur "Importer CSV", cette méthode est appelée pour permettre d'ajouter rapidement une liste d'étudiants à la promotion sans passer par l'interface de saisie)
    @FXML
    private void handleImportCSV() {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(studentTable.getScene().getWindow());
        if (file != null) {
            exportService.importFromCSV(file.getAbsolutePath(), studentDAO);
            refreshTable();
        }
    }

    @FXML // Méthodes pour gérer l'exportation de la liste des étudiants dans différents formats (CSV, JSON, XML), en utilisant le Service d'exportation pour générer les fichiers correspondants (ex: lorsque l'utilisateur clique sur "Exporter en CSV", "Exporter en JSON" ou "Exporter en XML", ces méthodes sont appelées pour créer les fichiers dans le format choisi)
    private void handleExportCSV() { exportToFile("export.csv", "CSV", "*.csv", "csv"); }
    @FXML
    private void handleExportJSON() { exportToFile("etudiants.json", "JSON", "*.json", "json"); }
    @FXML
    private void handleExportXML() { exportToFile("etudiants.xml", "XML", "*.xml", "xml"); }
    // Méthode utilitaire pour gérer l'exportation dans différents formats, en affichant une boîte de dialogue pour choisir l'emplacement de sauvegarde du fichier et en appelant le Service d'exportation avec les données actuelles de la TableView (ex: pour centraliser la logique d'exportation et éviter la duplication de code dans les différentes méthodes d'exportation)
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
    // Méthode utilitaire pour afficher une boîte de dialogue de sélection de fichier pour l'exportation, en configurant le nom de fichier par défaut, la description et le filtre d'extension (ex: pour permettre à l'utilisateur de choisir facilement où sauvegarder le fichier exporté et dans quel format)
    private File getSaveLocation(String defName, String desc, String ext) {
        FileChooser fc = new FileChooser();
        fc.setInitialFileName(defName);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(desc, ext));
        return fc.showSaveDialog(studentTable.getScene().getWindow());
    }
    // Méthodes utilitaires pour afficher des notifications d'information ou d'erreur à l'utilisateur, en utilisant des Alertes JavaFX (ex: pour informer l'utilisateur du succès d'une opération ou pour afficher un message d'erreur en cas de problème avec les données saisies ou les opérations effectuées)
    private void showNotification(String title, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(content); a.showAndWait();
    }
    // Méthode pour afficher une alerte d'erreur avec un titre et un message, en utilisant une Alert de type ERROR (ex: pour informer l'utilisateur d'un problème ou d'une erreur qui s'est produite, comme une validation échouée ou une opération qui n'a pas pu être effectuée)
    private void showError(String title, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(content); a.showAndWait();
    }
    // Méthode pour réinitialiser les filtres de recherche et les champs de saisie, ainsi que pour désélectionner tout étudiant dans la TableView (ex: lorsque l'utilisateur clique sur "Réinitialiser les filtres", cette méthode est appelée pour remettre l'interface dans son état initial, en effaçant les champs de recherche, les champs de saisie et en désélectionnant tout étudiant)
    @FXML
    private void handleResetFilters() {
        searchFirstName.clear(); searchLastName.clear();
        fNameInput.clear(); lNameInput.clear();
        ageInput.clear(); gradeInput.clear();
        ageFilterCombo.setValue("Tous");
        studentTable.getSelectionModel().clearSelection();
    }
    // Méthode pour gérer la déconnexion de l'utilisateur, en changeant la scène pour revenir à l'écran de connexion (ex: lorsque l'utilisateur clique sur "Déconnexion", cette méthode est appelée pour le ramener à l'écran de connexion et lui permettre de se reconnecter ou de changer d'utilisateur)
    @FXML
    private void handleLogout(ActionEvent event) {
        UIUtils.switchScene(event, "WelcomeView.fxml", "LP Tracker - Connexion");
    }
    // Méthode pour gérer le clic sur une ligne de la TableView, en affichant les détails de l'étudiant sélectionné dans les champs de saisie (ex: lorsque l'utilisateur clique sur un étudiant dans la liste, cette méthode est appelée pour remplir les champs de saisie avec les informations de l'étudiant sélectionné, afin de permettre une modification facile)
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
    // Méthode pour configurer l'infinite scrolling sur la TableView, en ajoutant un listener à la barre de défilement verticale pour charger automatiquement les données suivantes lorsque l'utilisateur atteint 90% du scroll (ex: pour permettre à l'utilisateur de faire défiler la liste des étudiants sans interruption, en chargeant les données par pages au fur et à mesure qu'il scroll)
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
    // Méthode pour charger la page suivante de données depuis la base de données et l'ajouter à la liste observable utilisée par la TableView, en mettant à jour le flag isLoading pour éviter les chargements multiples simultanés (ex: lorsque l'utilisateur atteint 90% du scroll, cette méthode est appelée pour charger les étudiants suivants et les afficher dans la liste)
    private void loadNextPage() {
        isLoading = true;
        currentPage++;
        List<Student> nextStudents = studentDAO.getStudentsPaged(currentPage, ROWS_PER_PAGE);
        if (nextStudents != null && !nextStudents.isEmpty()) masterData.addAll(nextStudents);
        isLoading = false;
    }
}