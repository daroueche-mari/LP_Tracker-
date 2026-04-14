package controller;
// Importations des classes nécessaires pour le fonctionnement du contrôleur (ex: pour gérer les événements, manipuler les composants de l'interface utilisateur, interagir avec les services et les DAO, etc.)
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
// Note : Le AuthService va interagir avec le UserDAO pour accéder à la base de données et gérer les opérations liées à l'authentification (ex: vérifier les informations d'identification, créer un nouvel utilisateur, etc.)
 @FXML
    private void handleLogin(ActionEvent event) { // Récupère les valeurs des champs de connexion, valide qu'ils ne sont pas vides, puis appelle le service d'authentification pour tenter de connecter l'utilisateur
        String username = loginUserField.getText().trim();
        String password = loginPassField.getText();
        // Validation de base : champs non vides
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("❌ Erreur : Veuillez remplir tous les champs.");
            return;
        }

        try {
            // L'AuthService va récupérer l'User via le DAO, 
            // puis comparer les hash avec Sel + Poivre.
            authService.login(username, password);
            // Si tout est OK, on affiche un message de succès et on change de scène pour accéder à l'application principale
            System.out.println("✅ Connexion réussie !");
            UIUtils.switchScene(event, "MainView.fxml", "LP Tracker - Gestion des Étudiants");
            // Note : Après une connexion réussie, on pourrait aussi stocker l'utilisateur connecté dans une session ou un contexte global pour l'utiliser dans les autres parties de l'application (ex: afficher le nom de l'utilisateur dans la barre de navigation, personnaliser l'expérience utilisateur, etc.)
        } catch (exception.AuthException e) {
            // Le message viendra de ton AuthService (ex: "Mot de passe incorrect")
            System.out.println("❌ " + e.getMessage());
        }
    }
    // Méthode pour gérer l'inscription d'un nouvel utilisateur, en récupérant les valeurs des champs d'inscription, en validant qu'ils ne sont pas vides et que les mots de passe correspondent, puis en appelant le service d'authentification pour tenter de créer un nouvel utilisateur (ex: lorsque l'utilisateur remplit le formulaire d'inscription et clique sur "S'inscrire", cette méthode est appelée pour valider les données et créer le compte)
    @FXML
    private void handleRegister(ActionEvent event) { // Récupère les valeurs des champs d'inscription, valide qu'ils ne sont pas vides et que les mots de passe correspondent, puis appelle le service d'authentification pour tenter de créer un nouvel utilisateur
        String username = regUserField.getText().trim();
        String password = regPassField.getText();
        String confirm = regConfirmField.getText();
        // Validation de base : champs non vides et mots de passe correspondants
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
            // Si tout est OK, on affiche un message de succès et on réinitialise les champs d'inscription
            System.out.println("✅ Inscription réussie !");
            regUserField.clear();
            regPassField.clear();
            regConfirmField.clear();
        // En cas d'erreur (ex: utilisateur déjà existant, mot de passe trop faible, etc.), le message d'erreur viendra de ton AuthService et sera affiché à l'utilisateur 
        } catch (exception.AuthException e) {
            System.out.println("❌ Erreur : " + e.getMessage());
        }
    }
    // Méthodes pour gérer les effets de survol sur les boutons, en changeant leur style ou leur taille pour les rendre plus interactifs (ex: lorsque la souris entre ou sort d'un bouton, ces méthodes sont appelées pour appliquer un style de survol ou un effet de zoom)
    @FXML
    private void handleHoverEnterSide(MouseEvent e) { // Change le style du bouton sur lequel la souris entre (effet de survol)
        ((Node)e.getSource()).setStyle("-fx-background-color: #3d5afe; -fx-border-color: white; -fx-border-radius: 25; -fx-text-fill: white; -fx-cursor: hand;");
    }
    // Note : Les boutons "Se connecter" et "S'inscrire" ont une classe CSS "side-button" qui applique un style de base, et ces méthodes ajoutent un effet de changement de couleur au survol pour les rendre plus interactifs.
    @FXML
    private void handleHoverExitSide(MouseEvent e) { // Réinitialise le style du bouton lorsque la souris sort de celui-ci
        ((Node)e.getSource()).setStyle("-fx-background-color: transparent; -fx-border-color: white; -fx-border-radius: 25; -fx-text-fill: white;");
    }
    // Note : Les boutons "Se connecter" et "S'inscrire" ont une classe CSS "side-button" qui applique un style de base, et ces méthodes ajoutent un effet de changement de couleur au survol pour les rendre plus interactifs.
    @FXML 
    private void handleHoverEnterGreen(MouseEvent e) {  // Agrandit légèrement le bouton sur lequel la souris entre pour donner un effet de survol plus dynamique
        ((Node)e.getSource()).setScaleX(1.05); 
        ((Node)e.getSource()).setScaleY(1.05); 
    }
    // Note : Le bouton "Se connecter" et "S'inscrire" ont une classe CSS "green-button" qui applique un style de base, et ces méthodes ajoutent un effet de zoom au survol pour les rendre plus interactifs.
    @FXML 
    private void handleHoverExitGreen(MouseEvent e) {  // Réinitialise la taille du bouton lorsque la souris sort de celui-ci
        ((Node)e.getSource()).setScaleX(1.0); 
        ((Node)e.getSource()).setScaleY(1.0); 
    }
}