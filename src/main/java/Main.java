import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. On charge maintenant LoginView au lieu de MainView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            Parent root = loader.load();

            // 2. On prépare une scène plus petite pour le login
            Scene scene = new Scene(root, 400, 350);

            // 3. Configuration de la fenêtre
            primaryStage.setTitle("Authentification - LP Tracker");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false); // On bloque la taille pour le login
            primaryStage.show();

            System.out.println("🔐 Écran de connexion chargé. En attente d'authentification...");

        } catch (Exception e) {
            System.err.println("❌ Erreur au démarrage : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}