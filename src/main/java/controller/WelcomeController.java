package controller;

import service.AuthService;
import view.HomeView;
import util.UIUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import exception.AuthException;

public class WelcomeController {

    // On utilise Object pour ne plus importer TextField/PasswordField
    @FXML private Object loginUserField, loginPassField;
    @FXML private Object regUserField, regPassField, regConfirmField;

    private AuthService authService = new AuthService();
    // --- GESTION DES ÉVÉNEMENTS (Appelent les méthodes du service et mettent à jour la vue) ---
    // Méthode de connexion. Elle récupère les données du formulaire, valide et connecte l'utilisateur via le service, puis change de scène.
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = HomeView.extractText(loginUserField);
        String password = HomeView.extractText(loginPassField);

        if (username.isEmpty() || password.isEmpty()) {
            HomeView.showWarning("Champs incomplets", "Merci de remplir tous les champs.");
            return;
        }

        try {
            authService.login(username, password);
            HomeView.showInfo("Bienvenue", "Connexion réussie !");
            UIUtils.switchScene(event, "MainView.fxml", "LP Tracker - Gestion des Étudiants");
        } catch (AuthException e) {
            HomeView.showError("Erreur d'accès", e.getMessage());
        }
    }
    // Méthode d'inscription. Elle récupère les données du formulaire, valide et crée le compte via le service, puis affiche un message de succès ou d'erreur.
   @FXML
    private void handleRegister(ActionEvent event) {
        String username = HomeView.extractText(regUserField);
        String password = HomeView.extractText(regPassField); // Pas de trim() sur les mots de passe !
        String confirm = HomeView.extractText(regConfirmField);

        // 1. Vérification des champs vides
        if (username.isEmpty() || password.isEmpty()) {
            HomeView.showWarning("Champs incomplets", "Veuillez remplir tous les champs.");
            return;
        }

        // 2. Vérification de la correspondance
        if (!password.equals(confirm)) {
            HomeView.showWarning("Saisie incorrecte", "Les mots de passe ne correspondent pas.");
            return;
        }

        // 3. Tentative d'inscription (Gestion des erreurs métier)
        try {
            authService.register(username, password);
            HomeView.showInfo("Bravo", "Compte créé ! Vous pouvez vous connecter.");
            HomeView.clearInputs(regUserField, regPassField, regConfirmField);
        } catch (AuthException e) {
            // C'est ici que tu gères les erreurs comme "L'utilisateur existe déjà"
            HomeView.showError("Erreur d'inscription", e.getMessage());
        }
    }
}