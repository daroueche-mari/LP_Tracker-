package service; // Vérifie bien que ce fichier est dans src/test/java/service

import controller.StudentController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

// Cette annotation répare tes erreurs sur @Mock et @InjectMocks
@ExtendWith(MockitoExtension.class)
public class ControllerTest extends ApplicationTest { // Répare clickOn

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController controller;

    @Override
    @Start // Répare @Start
    public void start(Stage stage) throws Exception {
        // Charge le FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
        loader.setControllerFactory(param -> controller);
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

   @Test
    void testAjouterBouton() throws Exception {
        // Utilise les IDs réels du FXML
        clickOn("#fNameInput").write("Jean");
        clickOn("#lNameInput").write("Dupont");
        clickOn("#ageInput").write("20");
        clickOn("#gradeInput").write("15");
        
        // On cherche le bouton par son texte car il n'a pas de fx:id
        clickOn("➕ Ajouter"); 

        // Vérification
        verify(studentService).validateAndAdd(eq("Jean"), eq("Dupont"), eq("20"), eq("15"));
    }
}