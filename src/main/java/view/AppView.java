package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Student;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppView {

    // --- CONFIGURATION DU TABLEAU ---
    // Cette méthode configure les colonnes d'une TableView pour afficher les propriétés d'un étudiant (ID, prénom, nom, âge, note) en utilisant des PropertyValueFactory. Elle prend un objet tableObj (généralement une TableView) et un nombre variable de colonnes à configurer.
    @SuppressWarnings("unchecked")
    public static void setupTable(Object tableObj, Object... cols) {
        // Le cast est nécessaire car on reçoit un Object depuis le Controller
        String[] props = {"id", "firstName", "lastName", "age", "grade"};
        
        for (int i = 0; i < cols.length; i++) {
            TableColumn<Student, ?> col = (TableColumn<Student, ?>) cols[i];
            col.setCellValueFactory(new PropertyValueFactory<>(props[i]));
        }
    }

    // --- GESTION DES FILTRES ---
    // Cette méthode configure les filtres de recherche (TextField pour le prénom et le nom, ComboBox pour l'âge) en attachant des listeners qui déclenchent une mise à jour de la TableView à chaque changement de valeur.
    @SuppressWarnings("unchecked")
    public static void setupFilters(Object comboObj, Object fNameObj, Object lNameObj, Runnable onUpdate) {
        ComboBox<String> combo = (ComboBox<String>) comboObj;
        TextField fName = (TextField) fNameObj;
        TextField lName = (TextField) lNameObj;

        ObservableList<String> ages = FXCollections.observableArrayList("Tous");
        for (int i = 18; i <= 70; i++) ages.add(String.valueOf(i));
        combo.setItems(ages);

        fName.textProperty().addListener((obs, old, val) -> onUpdate.run());
        lName.textProperty().addListener((obs, old, val) -> onUpdate.run());
        combo.valueProperty().addListener((obs, old, val) -> onUpdate.run());
    }

    // --- INFINITE SCROLL ---
    // Cette méthode ajoute un listener à la barre de défilement verticale d'une TableView pour détecter lorsque l'utilisateur atteint 90% du scroll, et déclenche une action (ex: charger plus d'étudiants) via le Runnable passé en paramètre.
    public static void setupScroll(Object tableObj, Runnable loadNext) {
        if (!(tableObj instanceof TableView<?> table)) return;
        
        javafx.application.Platform.runLater(() -> {
            for (Node n : table.lookupAll(".scroll-bar")) {
                if (n instanceof ScrollBar bar && bar.getOrientation() == Orientation.VERTICAL) {
                    bar.valueProperty().addListener((obs, old, val) -> {
                        if (val.doubleValue() > (bar.getMax() * 0.9)) loadNext.run();
                    });
                }
            }
        });
    }

    // --- DIALOGUES DE FICHIERS ---
    // Méthode pour afficher une boîte de dialogue d'ouverture de fichier, permettant à l'utilisateur de sélectionner un fichier CSV à importer. Elle prend un objet source pour obtenir la fenêtre parente, et retourne le chemin du fichier sélectionné ou null si l'utilisateur annule.
    public static String showOpenDialog(Object source) {
        if (!(source instanceof Node node)) return null;
        Stage stage = (Stage) node.getScene().getWindow();
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File f = fc.showOpenDialog(stage);
        return (f != null) ? f.getAbsolutePath() : null;
    }
    // Méthode pour afficher une boîte de dialogue de sauvegarde de fichier, permettant à l'utilisateur de choisir où enregistrer un fichier exporté (ex: les statistiques ou la liste des étudiants). Elle prend un objet source pour obtenir la fenêtre parente, et une extension de fichier pour suggérer un nom de fichier par défaut.
    public static String showSaveDialog(Object source, String ext) {
        if (!(source instanceof Node node)) return null;
        Stage stage = (Stage) node.getScene().getWindow();
        FileChooser fc = new FileChooser();
        fc.setInitialFileName("export." + ext);
        File f = fc.showSaveDialog(stage);
        return (f != null) ? f.getAbsolutePath() : null;
    }

    // --- STATISTIQUES ET RENDU ---
    // Méthode pour afficher les statistiques globales et par groupe d'âge dans des labels dédiés, avec une mise en forme conditionnelle pour la moyenne (ex: en vert si la moyenne est supérieure ou égale à 10, sinon en rouge).
    public static void renderStats(Object countLbl, Object avgLbl, int count, double avg) {
        if (countLbl instanceof Label cl) cl.setText(String.valueOf(count));
        if (avgLbl instanceof Label al) {
            al.setText(String.format("%.2f / 20", avg));
            al.setStyle("-fx-text-fill: " + (avg >= 10 ? "#27ae60" : "#e74c3c") + "; -fx-font-weight: bold;");
        }
    }

    // --- MANIPULATION DES DONNÉES TABLEAU ---
    // Méthode pour remplacer entièrement les éléments d'une TableView par une nouvelle liste d'étudiants, utile après une opération de filtrage ou de rafraîchissement complet des données.
    @SuppressWarnings("unchecked")
    public static void setTableItems(Object table, List<Student> items) {
        if (table instanceof TableView) {
            ((TableView<Student>) table).getItems().setAll(items);
        }
    }
    // Méthode pour ajouter une liste d'étudiants à la TableView sans écraser les éléments existants, utile pour les opérations de pagination ou d'ajout en masse.
    @SuppressWarnings("unchecked")
    public static void addTableItems(Object table, List<Student> items) {
        if (table instanceof TableView) {
            ((TableView<Student>) table).getItems().addAll(items);
        }
    }
    // Méthode pour récupérer la liste complète des étudiants actuellement affichés dans la TableView, utile pour les opérations de filtrage, de tri, ou d'exportation.
    @SuppressWarnings("unchecked")
    public static List<Student> getTableItems(Object table) {
        return ((TableView<Student>) table).getItems();
    }
    // Méthode pour importer une liste d'étudiants à partir d'un fichier CSV. Elle lit le fichier ligne par ligne, nettoie les données, crée des objets Student, et les ajoute à la base de données via le DAO.
    @SuppressWarnings("unchecked")
    public static Object getSelectedItem(Object table) {
        return ((TableView<Student>) table).getSelectionModel().getSelectedItem();
    }

    // --- ALERTES ---
    public static void notifySuccess(String msg) { showAlert(Alert.AlertType.INFORMATION, "Succès", msg); }
    public static void notifyError(String title, String msg) { showAlert(Alert.AlertType.ERROR, title, msg); }
    public static void notifyWarning(String title, String msg) { showAlert(Alert.AlertType.WARNING, title, msg); }
    // Méthode générique pour afficher une alerte avec un type, un titre et un message personnalisés. Utilisée par les méthodes de notification spécifiques pour éviter la répétition de code.
    private static void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // --- UTILITAIRES FORMULAIRE ---
    // Méthode pour récupérer le texte d'un TextField, avec une vérification de type et une valeur par défaut de chaîne vide si l'objet n'est pas un TextField ou si le texte est null.
    public static String getText(Object input) { 
        return (input instanceof TextField tf) ? tf.getText() : ""; 
    }
    // Méthode pour récupérer la valeur sélectionnée d'une ComboBox, avec une valeur par défaut de "Tous" si l'objet n'est pas une ComboBox ou si aucune valeur n'est sélectionnée.
    public static String getValue(Object combo) { 
        return (combo instanceof ComboBox<?> cb) ? (String) cb.getValue() : "Tous"; 
    }
    // Méthode pour effacer le contenu de plusieurs champs de saisie (TextField) en une seule fois, facilitant ainsi la réinitialisation des formulaires après une opération d'ajout ou de modification.
    public static void clearFields(Object... fields) {
        for (Object f : fields) {
            if (f instanceof TextField tf) tf.clear();
        }
    }
    // Méthode pour remplir les champs d'un formulaire avec les données d'un étudiant sélectionné, facilitant ainsi la modification des informations de l'étudiant.
    public static void fillForm(Student s, Object... fields) {
        if (s == null || fields.length < 4) return;
        if (fields[0] instanceof TextField f1) f1.setText(s.getFirstName());
        if (fields[1] instanceof TextField f2) f2.setText(s.getLastName());
        if (fields[2] instanceof TextField f3) f3.setText(String.valueOf(s.getAge()));
        if (fields[3] instanceof TextField f4) f4.setText(String.valueOf(s.getGrade()));
    }
    // --- STATISTIQUES DYNAMIQUES ---
    // Méthode pour calculer et formater les statistiques globales et par groupe d
    @SuppressWarnings("unchecked")
    public static void filterTable(Object table, List<Student> masterData, String fName, String lName, String ageVal) {
        if (!(table instanceof TableView)) return;

        // Filtrage en mémoire (Ultra-rapide)
        List<Student> filtered = masterData.stream()
            .filter(s -> s.getFirstName().toLowerCase().contains(fName.toLowerCase()))
            .filter(s -> s.getLastName().toLowerCase().contains(lName.toLowerCase()))
            .filter(s -> {
                if (ageVal == null || ageVal.equals("Tous")) return true;
                return String.valueOf(s.getAge()).equals(ageVal);
            })
            .toList();

        // Mise à jour de la TableView
        ((TableView<Student>) table).getItems().setAll(filtered);
    }
    // --- CONFIRMATION ET SÉLECTION ---
    // Méthode pour afficher une boîte de dialogue de confirmation avant de supprimer un étudiant, retournant true si l'utilisateur confirme l'action, et false sinon.
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // On récupère la réponse de l'utilisateur
        java.util.Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    // Méthode pour sélectionner un étudiant dans la TableView et faire défiler jusqu'à lui, utile après une opération de mise à jour ou d'ajout pour mettre en évidence l'étudiant concerné.
    public static void selectAndScroll(Object tableObj, Object item) {
    if (tableObj instanceof TableView) {
        @SuppressWarnings("unchecked")
        TableView<Object> table = (TableView<Object>) tableObj;
        table.getSelectionModel().select(item);
        table.scrollTo(item);
    }
}
// Méthode pour réinitialiser la sélection d'une ComboBox à une valeur spécifique, utile pour remettre les filtres à zéro ou à une valeur par défaut.
public static void resetComboBox(Object comboObj, String value) {
    if (comboObj instanceof ComboBox) {
        @SuppressWarnings("unchecked")
        ComboBox<String> combo = (ComboBox<String>) comboObj;
        combo.getSelectionModel().select(value);
    }
}
// Méthode pour activer la sélection multiple dans la TableView, permettant à l'utilisateur de sélectionner plusieurs étudiants à la fois (ex: pour des opérations de suppression ou d'exportation en masse).
public static void enableMultipleSelection(Object tableObj) {
    if (tableObj instanceof TableView) {
        ((TableView<?>) tableObj).getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
}
// Méthode pour récupérer la liste des étudiants sélectionnés dans la TableView, utile pour les opérations de suppression ou d'exportation en masse.
public static List<Student> getSelectedStudents(Object tableObj) {
    if (tableObj instanceof TableView) {
        @SuppressWarnings("unchecked")
        TableView<Student> table = (TableView<Student>) tableObj;
        return new ArrayList<>(table.getSelectionModel().getSelectedItems());
    }
    return new ArrayList<>();
}
// --- OPTIMISATION DE LA PERFORMANCE DU TABLEAU ---
// Cette méthode applique des optimisations pour améliorer les performances d'affichage et de scroll dans la TableView, surtout lorsque le nombre d'étudiants devient important.
public static void optimizeTablePerformance(Object tableObj) {
    if (tableObj instanceof TableView<?> table) { // Utilisation du Pattern Matching (Java 16+)
        
        // 1. Fixe la taille des cellules pour un scroll fluide
        table.setFixedCellSize(30.0);
        
        // 2. Nouvelle version non-dépréciée de la politique de redimensionnement
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        
        System.out.println("⚡ [AppView] Performance du tableau optimisée.");
    }
}
}