package util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.io.File;
import java.io.IOException;

public class UIUtils {

    // 1. Affiche une boîte de dialogue
    public static void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // 2. Change de fenêtre VIA un événement (Bouton cliqué)
    public static void switchScene(ActionEvent event, String fxmlFile, String title) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        loadScene(stage, fxmlFile, title);
    }

    // 3. Change de fenêtre VIA un Node (TextField, TableView, etc.)
    // Indispensable pour ton LoginController et StudentController !
    public static void switchScene(Node node, String fxmlFile, String title) {
        Stage stage = (Stage) node.getScene().getWindow();
        loadScene(stage, fxmlFile, title);
    }

    // Méthode privée pour centraliser le chargement et éviter la répétition
    private static void loadScene(Stage stage, String fxmlFile, String title) {
        try {
            Parent root = FXMLLoader.load(UIUtils.class.getResource("/view/" + fxmlFile));
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.centerOnScreen(); // Optionnel : centre la fenêtre
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Erreur de chargement FXML (" + fxmlFile + ") : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 4. Sélection de fichier (Import)
    public static String openFileChooser(Window window, String title, String description) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, "*.csv", "*.txt"));
        File file = fileChooser.showOpenDialog(window);
        return (file != null) ? file.getAbsolutePath() : null;
    }

    // 5. Sauvegarde de fichier (Export)
    public static String saveFileChooser(Window window, String title, String defaultName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialFileName(defaultName);
        File file = fileChooser.showSaveDialog(window);
        return (file != null) ? file.getAbsolutePath() : null;
    }
}