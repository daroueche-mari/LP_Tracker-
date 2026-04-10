package service;

import model.Student;
import dao.StudentDAO;
import java.io.*;
import java.util.List;

public class ExportService {
    
    public void exportToCSV(List<Student> students, String filePath) {
        try (PrintWriter writer = new PrintWriter(new File(filePath))) {
            writer.println("Prenom,Nom,Age,Note");
            for (Student s : students) {
                writer.printf("%s,%s,%d,%.2f%n", s.getFirstName(), s.getLastName(), s.getAge(), s.getGrade());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Ajout de la méthode d'importation manquante
    public void importFromCSV(String filePath, StudentDAO dao) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Sauter l'en-tête
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4) {
                    Student s = new Student(0, data[0], data[1], Integer.parseInt(data[2]), Double.parseDouble(data[3]));
                    dao.addStudent(s);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // On garde ton nom exportToHTML (plus court)
    public void exportToHTML(List<Student> students, String stats, String filePath) {
        try (PrintWriter writer = new PrintWriter(new File(filePath))) {
            writer.println("<html><head><style>body{font-family:sans-serif;} table{border-collapse:collapse;width:100%;} th,td{border:1px solid #ddd;padding:8px;} th{background-color:#e67e22;color:white;}</style></head><body>");
            writer.println("<h1>Rapport LP Tracker</h1>");
            writer.println("<table><tr><th>Nom</th><th>Prénom</th><th>Note</th></tr>");
            for (Student s : students) {
                writer.println("<tr><td>" + s.getLastName() + "</td><td>" + s.getFirstName() + "</td><td>" + s.getGrade() + "/20</td></tr>");
            }
            writer.println("</table><p><b>" + stats + "</b></p></body></html>");
        } catch (IOException e) { e.printStackTrace(); }
    }
}