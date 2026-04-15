package controller;

import dao.UserDAO;
import model.User;
import service.AuthService;
import util.UIUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserDAO userDAO = new UserDAO();
    private AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showErrorMessage("Veuillez remplir tous les champs.");
            return;
        }

        User user = userDAO.getUserByUsername(username);

        if (user != null && authService.verifyLogin(password, user.getPasswordHash(), user.getSalt())) {
            System.out.println("✅ Authentification réussie pour : " + username);
            
            // On redirige vers la vue principale (StudentView ou MainView selon ton fichier)
            UIUtils.switchScene(
                usernameField, 
                "MainView.fxml", 
                "LP Tracker - Gestion des Étudiants"
            );
            
        } else {
            showErrorMessage("Identifiants incorrects !");
        }
    }

    /**
     * AJOUT : Indispensable pour le bouton retour dans LoginView.fxml
     */
    @FXML
    private void handleBackToWelcome(ActionEvent event) {
        UIUtils.switchScene(event, "WelcomeView.fxml", "LP Tracker - Bienvenue");
    }

    @FXML
    private void handleGoToRegister(ActionEvent event) {
        // CORRECTION : On va bien vers l'inscription, pas l'accueil
        UIUtils.switchScene(event, "RegisterView.fxml", "LP Tracker - Inscription");
    }

    private void showErrorMessage(String message) {
        errorLabel.setText(message);
        usernameField.setStyle("-fx-border-color: #e74c3c;"); 
        passwordField.setStyle("-fx-border-color: #e74c3c;");
    }
}