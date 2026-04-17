package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Student;
import java.util.List;

public class StatChartView {
    // Cette classe est dédiée à l'affichage d'un graphique circulaire (PieChart) représentant la répartition des notes des étudiants. Elle contient une méthode statique display qui prend une liste d'étudiants, analyse leurs notes pour compter le nombre d'excellents, de biens, de passables et d'échecs, puis crée et affiche un PieChart avec ces données.
    public static void display(List<Student> students) {
        Stage window = new Stage();

        // Bloque les interactions avec les autres fenêtres jusqu'à la fermeture
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Répartition des notes");
        window.setMinWidth(500);
        window.setMinHeight(550);

        // 1. Analyse des données
        int excellence = 0, bien = 0, passable = 0, echec = 0;

        for (Student s : students) {
            double g = s.getGrade();
            if (g >= 16) excellence++;
            else if (g >= 12) bien++;
            else if (g >= 10) passable++;
            else echec++;
        }

        // 2. Création des données du graphique
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
            new PieChart.Data("Excellent (16-20) : " + excellence, excellence),
            new PieChart.Data("Bien (12-16) : " + bien, bien),
            new PieChart.Data("Passable (10-12) : " + passable, passable),
            new PieChart.Data("Échec (< 10) : " + echec, echec)
        );

        // 3. Configuration du PieChart
        PieChart chart = new PieChart(pieData);
        chart.setTitle("Performance de la Promotion");
        chart.setLegendSide(Side.BOTTOM); // Tes carrés de couleur en bas
        chart.setLabelsVisible(true);
        chart.setClockwise(true);

        // 4. Mise en page
        VBox layout = new VBox(10);
        layout.getChildren().add(chart);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(layout);
        // On importe ton CSS pour appliquer les couleurs définies précédemment
        try {
            scene.getStylesheets().add(StatChartView.class.getResource("/view/style.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("🎨 CSS non trouvé pour le graphique, utilisation des couleurs par défaut.");
        }

        window.setScene(scene);
        window.showAndWait();
    }
}