import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
// Tes imports personnalisés
import model.Student; 
import dao.StudentDAO;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("LP Tracker - Ajouter un Étudiant");

        // 1. Création des composants de l'interface (Vue)
        TextField fNameInput = new TextField(); 
        fNameInput.setPromptText("Prénom");
        
        TextField lNameInput = new TextField(); 
        lNameInput.setPromptText("Nom");
        
        TextField ageInput = new TextField(); 
        ageInput.setPromptText("Âge");
        
        TextField gradeInput = new TextField(); 
        gradeInput.setPromptText("Note");
        
        Button btnAdd = new Button("Enregistrer l'étudiant");

        // On prépare le DAO (Action)
        StudentDAO dao = new StudentDAO();

        // 2. Action du bouton (Lien entre Vue, Modèle et DAO)
        btnAdd.setOnAction(e -> {
            try {
                // On crée l'objet Student avec les infos saisies
                Student s = new Student(
                    fNameInput.getText(),
                    lNameInput.getText(),
                    Integer.parseInt(ageInput.getText()),
                    Double.parseDouble(gradeInput.getText())
                );

                // On demande au DAO de l'envoyer en base
                dao.addStudent(s);
                
                System.out.println("✅ Étudiant ajouté !");
                
                // Nettoyage des champs après l'ajout
                fNameInput.clear(); 
                lNameInput.clear(); 
                ageInput.clear(); 
                gradeInput.clear();

            } catch (NumberFormatException ex) {
                System.err.println("❌ Erreur : L'âge et la note doivent être des nombres.");
            }
        });

        // 3. Mise en page (Layout)
        VBox layout = new VBox(10); // Espacement de 10px
        layout.getChildren().addAll(
            new Label("Nouvel Étudiant"), 
            fNameInput, 
            lNameInput, 
            ageInput, 
            gradeInput, 
            btnAdd
        );
        layout.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(layout, 350, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        System.out.println("✅ Interface de saisie lancée avec succès.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}