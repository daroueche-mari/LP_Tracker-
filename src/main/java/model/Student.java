package model;
// Classe modèle représentant un étudiant, avec des propriétés privées et des getters/setters publics pour accéder à ces propriétés (ex: pour stocker les informations d'un étudiant récupérées de la base de données ou pour créer un nouvel étudiant à partir des données saisies dans l'interface utilisateur)
public class Student {
    private int id;
    private String firstName;
    private String lastName;
    private int age;
    private double grade;

    // Constructeur vide (utile pour certaines bibliothèques plus tard)
    public Student() {}

    // Constructeur complet (pour quand on récupère un étudiant de la DB)
    public Student(int id, String firstName, String lastName, int age, double grade) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.grade = grade;
    }

    // Constructeur sans ID (pour quand on veut CRÉER un nouvel étudiant, l'ID est géré par SQL)
    public Student(String firstName, String lastName, int age, double grade) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.grade = grade;
    }

    // --- GETTERS & SETTERS (Indispensables pour accéder aux données privées) ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public double getGrade() { return grade; }
    public void setGrade(double grade) { this.grade = grade; }

    // Pour afficher l'étudiant facilement dans la console
    @Override
    public String toString() { 
        return "Student [id=" + id + ", name=" + firstName + " " + lastName + ", age=" + age + ", grade=" + grade + "]";
    }
}