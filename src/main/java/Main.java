import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. On charge le fichier FXML (le design)
            // Assure-toi que le fichier est bien dans src/main/resources/view/MainView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
            Parent root = loader.load();

            // 2. On prépare la scène
            Scene scene = new Scene(root, 500, 600);

            // 3. On affiche la fenêtre
            primaryStage.setTitle("LP Tracker - Student Management");
            primaryStage.setScene(scene);
            primaryStage.show();

            System.out.println("✅ Vue FXML chargée avec succès !");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement de la vue FXML : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}