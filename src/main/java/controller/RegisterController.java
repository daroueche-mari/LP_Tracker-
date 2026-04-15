package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import service.AuthService;
import exception.AuthException;
import util.UIUtils;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    
    private final AuthService authService = new AuthService();

    @FXML
    private void handleRegister() {
        try {
            authService.register(usernameField.getText(), passwordField.getText());
            UIUtils.showAlert("Succès", "Compte créé avec succès !", Alert.AlertType.INFORMATION);
            // Rediriger vers le login ou fermer la fenêtre
        } catch (AuthException e) {
            UIUtils.showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void handleBackToLogin(ActionEvent event) {
        UIUtils.switchScene(event, "WelcomeView.fxml", "LP Tracker - Connexion");
    }
}