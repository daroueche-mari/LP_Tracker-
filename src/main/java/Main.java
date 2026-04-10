import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. On charge l'écran de bienvenue (WelcomeView)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/WelcomeView.fxml"));
            Parent root = loader.load();

            // 2. On prépare une scène adaptée au design (800x500 comme dans le FXML)
            Scene scene = new Scene(root, 800, 500);

            // 3. Configuration de la fenêtre
            primaryStage.setTitle("Bienvenue - LP Tracker");
            primaryStage.setScene(scene);
            
            // On peut autoriser le redimensionnement ou pas selon tes goûts
            primaryStage.setResizable(true); 
            primaryStage.centerOnScreen(); // Pour qu'elle apparaisse au milieu
            primaryStage.show();

            System.out.println("🚀 Application lancée sur l'écran d'accueil.");

        } catch (Exception e) {
            System.err.println("❌ Erreur au démarrage : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}