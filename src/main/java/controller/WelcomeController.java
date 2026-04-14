package controller;

import service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import util.UIUtils;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class WelcomeController { // Contrôleur pour la page de bienvenue (connexion/inscription)

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
    private void handleLogin(ActionEvent event) { // Récupère les valeurs des champs de connexion, valide qu'ils ne sont pas vides, puis appelle le service d'authentification pour tenter de connecter l'utilisateur
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
    private void handleRegister(ActionEvent event) { // Récupère les valeurs des champs d'inscription, valide qu'ils ne sont pas vides et que les mots de passe correspondent, puis appelle le service d'authentification pour tenter de créer un nouvel utilisateur
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
    private void handleHoverEnterSide(MouseEvent e) { // Change le style du bouton sur lequel la souris entre (effet de survol)
        ((Node)e.getSource()).setStyle("-fx-background-color: #3d5afe; -fx-border-color: white; -fx-border-radius: 25; -fx-text-fill: white; -fx-cursor: hand;");
    }

    @FXML
    private void handleHoverExitSide(MouseEvent e) { // Réinitialise le style du bouton lorsque la souris sort de celui-ci
        ((Node)e.getSource()).setStyle("-fx-background-color: transparent; -fx-border-color: white; -fx-border-radius: 25; -fx-text-fill: white;");
    }

    @FXML 
    private void handleHoverEnterGreen(MouseEvent e) {  // Agrandit légèrement le bouton sur lequel la souris entre pour donner un effet de survol plus dynamique
        ((Node)e.getSource()).setScaleX(1.05); 
        ((Node)e.getSource()).setScaleY(1.05); 
    }

    @FXML 
    private void handleHoverExitGreen(MouseEvent e) {  // Réinitialise la taille du bouton lorsque la souris sort de celui-ci
        ((Node)e.getSource()).setScaleX(1.0); 
        ((Node)e.getSource()).setScaleY(1.0); 
    }
}