// Importations des classes nécessaires pour le fonctionnement de la classe principale de l'application JavaFX (ex: pour lancer l'application, charger les scènes, etc.)
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
// Importations des classes nécessaires pour le fonctionnement de la classe principale de l'application JavaFX (ex: pour lancer l'application, charger les scènes, etc.)
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Chargement de l'écran de bienvenue
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/WelcomeView.fxml"));
            Parent root = loader.load();

            // 2. Préparation de la scène
            Scene scene = new Scene(root, 800, 500);

            // 3. Configuration de la fenêtre et AJOUT DE L'ICÔNE 🚀
            primaryStage.setTitle("Bienvenue - LP Tracker");
            
            // Cette ligne charge ton logo comme icône officielle de l'application
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
            
            primaryStage.setScene(scene);

            // 4. Ajustement et affichage
            primaryStage.setResizable(true); 
            primaryStage.centerOnScreen();
            primaryStage.show();

            System.out.println("🚀 Application lancée avec icône personnalisée.");

        } catch (Exception e) {
            System.err.println("❌ Erreur au démarrage : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) { // Méthode principale qui lance l'application JavaFX (ex: lorsque l'utilisateur double-clique sur le JAR ou exécute la classe Main pour démarrer l'application)
        launch(args);
    }
}