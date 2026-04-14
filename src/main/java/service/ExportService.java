package service;

import model.Student;
import dao.StudentDAO;
import java.io.*;
import java.util.List;
import java.util.Locale;

public class ExportService {

    // --- CSV ---
    public void exportToCSV(List<Student> students, String filePath) { // Méthode pour exporter la liste des étudiants dans un fichier CSV (ex: pour partager les données de la promotion dans un format simple et largement compatible avec d'autres outils comme Excel ou Google Sheets)
        // Utilisation de Locale.US pour forcer le point comme séparateur décimal dans le CSV
        try (PrintWriter writer = new PrintWriter(new File(filePath))) {
            writer.println("Prenom,Nom,Age,Note");
            for (Student s : students) {
                writer.format(Locale.US, "%s,%s,%d,%.2f%n", 
                    s.getFirstName(), s.getLastName(), s.getAge(), s.getGrade());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void importFromCSV(String filePath, StudentDAO dao) { // Méthode pour importer des étudiants depuis un fichier CSV (ex: pour ajouter rapidement une liste d'étudiants à la promotion sans passer par l'interface de saisie)
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Sauter l'en-tête
            while ((line = br.readLine()) != null) {
                // On gère les fichiers qui pourraient utiliser des virgules ou points-virgules
                String[] data = line.split("[,;]"); 
                if (data.length >= 4) {
                    String fName = data[0].trim();
                    String lName = data[1].trim();
                    int age = Integer.parseInt(data[2].trim());
                    // Remplacement de la virgule par un point pour le parsing du Double
                    double grade = Double.parseDouble(data[3].trim().replace(",", "."));
                    
                    dao.addStudent(new Student(0, fName, lName, age, grade));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- JSON ---
    public void exportToJSON(List<Student> students, String filePath) { // Méthode pour exporter la liste des étudiants dans un fichier JSON (ex: pour partager les données de la promotion dans un format moderne et facilement intégrable avec d'autres systèmes ou applications)
        try (PrintWriter writer = new PrintWriter(new File(filePath))) {
            writer.println("[");
            for (int i = 0; i < students.size(); i++) {
                Student s = students.get(i);
                writer.format(Locale.US, "  { \"firstName\": \"%s\", \"lastName\": \"%s\", \"age\": %d, \"grade\": %.2f }%s%n",
                        s.getFirstName(), s.getLastName(), s.getAge(), s.getGrade(),
                        (i < students.size() - 1) ? "," : "");
            }
            writer.println("]");
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- XML ---
    public void exportToXML(List<Student> students, String filePath) { // Méthode pour exporter la liste des étudiants dans un fichier XML (ex: pour partager les données de la promotion dans un format structuré ou pour une intégration avec d'autres systèmes qui utilisent XML)
        try (PrintWriter writer = new PrintWriter(new File(filePath))) {
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.println("<students>");
            for (Student s : students) {
                writer.println("  <student>");
                writer.println("    <firstName>" + s.getFirstName() + "</firstName>");
                writer.println("    <lastName>" + s.getLastName() + "</lastName>");
                writer.println("    <age>" + s.getAge() + "</age>");
                writer.println("    <grade>" + s.getGrade() + "</grade>");
                writer.println("  </student>");
            }
            writer.println("</students>");
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- HTML ---
    public void exportToHTML(List<Student> students, String stats, String filePath) { // Méthode pour exporter la liste des étudiants et les statistiques de la promotion dans un fichier HTML (ex: pour générer un rapport visuel de la promotion à partager ou à imprimer)
        try (PrintWriter writer = new PrintWriter(new File(filePath))) {
            writer.println("<html><head><meta charset='UTF-8'><style>body{font-family:sans-serif; padding:20px;} table{border-collapse:collapse;width:100%;} th,td{border:1px solid #ddd;padding:8px;text-align:left;} th{background-color:#2c3e50;color:white;}</style></head><body>");
            writer.println("<h1>Rapport de Promotion - LP Tracker</h1>");
            writer.println("<div style='background:#f9f9f9; padding:10px; border-radius:5px; margin-bottom:20px;'>" + stats.replace("\n", "<br>") + "</div>");
            writer.println("<table><tr><th>Prénom</th><th>Nom</th><th>Âge</th><th>Note</th></tr>");
            for (Student s : students) {
                writer.println("<tr><td>" + s.getFirstName() + "</td><td>" + s.getLastName() + "</td><td>" + s.getAge() + "</td><td>" + s.getGrade() + "/20</td></tr>");
            }
            writer.println("</table></body></html>");
        } catch (IOException e) { e.printStackTrace(); }
    }
}