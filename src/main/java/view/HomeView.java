package view;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.io.InputStream;
import java.net.URL;

public class HomeView {

    // Pour les pseudos (on enlève les espaces accidentels)
    // Cette méthode prend un objet d'entrée (généralement un TextField), vérifie s'il s'agit d'un contrôle de saisie de texte, et retourne le texte saisi sans les espaces en début ou fin. Utile pour éviter les erreurs de validation dues à des espaces involontaires.
    public static String extractText(Object input) {
        if (input instanceof TextInputControl) {
            String text = ((TextInputControl) input).getText();
            return (text != null) ? text.trim() : "";
        }
        return "";
    }

    // NOUVEAU : Pour les mots de passe (on garde tout !)  
    // Utile pour éviter les problèmes de mot de passe avec des espaces en début ou fin, qui sont souvent involontaires et peuvent causer des erreurs de connexion difficiles à comprendre pour l'utilisateur.
    public static String extractRawText(Object input) {
        if (input instanceof TextInputControl) {
            String text = ((TextInputControl) input).getText();
            return (text != null) ? text : "";
        }
        return "";
    }
    // Méthodes d'affichage de messages à l'utilisateur, centralisées pour garantir une cohérence visuelle et une expérience utilisateur optimale. Elles utilisent des alertes JavaFX avec des titres et des icônes adaptés au type de message (information, avertissement, erreur).
    public static void clearInputs(Object... inputs) {
        for (Object input : inputs) {
            if (input instanceof TextInputControl) {
                ((TextInputControl) input).clear();
            }
        }
    }
    // Méthodes d'affichage de messages à l'utilisateur, centralisées pour garantir une cohérence visuelle et une expérience utilisateur optimale. Elles utilisent des alertes JavaFX avec des titres et des icônes adaptés au type de message (information, avertissement, erreur).
    public static void showInfo(String t, String m) { showAlert(Alert.AlertType.INFORMATION, t, m); }
    public static void showWarning(String t, String m) { showAlert(Alert.AlertType.WARNING, t, m); }
    public static void showError(String t, String m) { showAlert(Alert.AlertType.ERROR, t, m); }
    // Méthode privée pour centraliser la création des alertes et éviter la répétition de code, tout en assurant une cohérence visuelle (icône, CSS) et une bonne expérience utilisateur.
    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        try {
            InputStream iconStream = HomeView.class.getResourceAsStream("/view/icon.png");
            if (iconStream != null) stage.getIcons().add(new Image(iconStream));

            URL cssUrl = HomeView.class.getResource("/view/style.css");
            if (cssUrl != null) {
                alert.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
                alert.getDialogPane().getStyleClass().add("my-alert");
            }
        } catch (Exception e) {
            System.err.println("🎨 Erreur CSS Alerte : " + e.getMessage());
        }
        alert.showAndWait();
    }
}