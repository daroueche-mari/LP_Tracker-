package controller;

import service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import util.UIUtils;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class WelcomeController {

    // --- Champs Connexion (Partie DROITE - Orange) ---
    @FXML private TextField loginUserField;
    @FXML private PasswordField loginPassField;

    // --- Champs Inscription (Partie GAUCHE - Bleu) ---
    @FXML private TextField regUserField;
    @FXML private PasswordField regPassField;
    @FXML private PasswordField regConfirmField;
    // --- Services et DAO ---
    private AuthService authService = new AuthService();

 @FXML
    private void handleLogin(ActionEvent event) {
        String username = loginUserField.getText().trim();
        String password = loginPassField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("❌ Erreur : Veuillez remplir tous les champs.");
            return;
        }

        try {
            // L'AuthService va récupérer l'User via le DAO, 
            // puis comparer les hash avec Sel + Poivre.
            authService.login(username, password);
            
            System.out.println("✅ Connexion réussie !");
            UIUtils.switchScene(event, "MainView.fxml", "LP Tracker - Gestion des Étudiants");

        } catch (exception.AuthException e) {
            // Le message viendra de ton AuthService (ex: "Mot de passe incorrect")
            System.out.println("❌ " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = regUserField.getText().trim();
        String password = regPassField.getText();
        String confirm = regConfirmField.getText();

        if (!password.equals(confirm)) {
            System.out.println("❌ Erreur : Les mots de passe ne correspondent pas.");
            return;
        }

        try {
            // L'AuthService va :
            // 1. Vérifier si l'user existe déjà
            // 2. Valider la complexité du pass (min 8 car, etc.)
            // 3. Générer le Sel et hasher avec le Poivre
            // 4. Appeler userDAO.saveUser
            authService.register(username, password);
            
            System.out.println("✅ Inscription réussie !");
            regUserField.clear();
            regPassField.clear();
            regConfirmField.clear();
            
        } catch (exception.AuthException e) {
            System.out.println("❌ Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void handleHoverEnterSide(MouseEvent e) {
        ((Node)e.getSource()).setStyle("-fx-background-color: #3d5afe; -fx-border-color: white; -fx-border-radius: 25; -fx-text-fill: white; -fx-cursor: hand;");
    }

    @FXML
    private void handleHoverExitSide(MouseEvent e) {
        ((Node)e.getSource()).setStyle("-fx-background-color: transparent; -fx-border-color: white; -fx-border-radius: 25; -fx-text-fill: white;");
    }

    @FXML 
    private void handleHoverEnterGreen(MouseEvent e) { 
        ((Node)e.getSource()).setScaleX(1.05); 
        ((Node)e.getSource()).setScaleY(1.05); 
    }

    @FXML 
    private void handleHoverExitGreen(MouseEvent e) { 
        ((Node)e.getSource()).setScaleX(1.0); 
        ((Node)e.getSource()).setScaleY(1.0); 
    }
}