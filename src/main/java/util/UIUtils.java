package util;

import javafx.animation.FadeTransition;
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
import javafx.util.Duration;

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
        // 1. Vérification du chemin
        java.net.URL resource = UIUtils.class.getResource("/view/" + fxmlFile);
        
        if (resource == null) {
            System.err.println("❌ ERREUR CRITIQUE : Le fichier FXML est introuvable !");
            System.err.println("   Chemin tenté : /view/" + fxmlFile);
            System.err.println("   Vérifiez l'orthographe et les majuscules dans le dossier resources/view/");
            return; // On arrête avant le crash
        }

        Parent root = FXMLLoader.load(resource);
        
        // 2. Préparer l'opacité pour le fondu
        root.setOpacity(0);
        
        // 3. Configurer la scène et le stage
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setResizable(true);
        
        // 4. Ajuster la taille et centrer
        stage.sizeToScene(); 
        stage.centerOnScreen();

        // 5. Afficher la fenêtre
        stage.show();

        // 6. Lancer l'animation
        FadeTransition ft = new FadeTransition(Duration.millis(800), root);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

    } catch (IOException e) {
        System.err.println("❌ Erreur de lecture du fichier FXML : " + e.getMessage());
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