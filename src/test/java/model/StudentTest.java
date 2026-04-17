package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    @Test
    void testStudentConstructorAndGetters() {
        // Test du constructeur complet
        Student s = new Student(1, "Jean", "Dupont", 20, 15.5);

        assertEquals(1, s.getId());
        assertEquals("Jean", s.getFirstName());
        assertEquals("Dupont", s.getLastName());
        assertEquals(20, s.getAge());
        assertEquals(15.5, s.getGrade());
    }

    @Test
    void testSetters() {
        Student s = new Student();

        s.setId(10);
        s.setFirstName("Alice");
        s.setLastName("Wonderland");
        s.setAge(22);
        s.setGrade(18.0);

        assertEquals(10, s.getId());
        assertEquals("Alice", s.getFirstName());
        assertEquals("Wonderland", s.getLastName());
        assertEquals(22, s.getAge());
        assertEquals(18.0, s.getGrade());
    }

    @Test
    void testToString() {
        Student s = new Student(1, "Jean", "Dupont", 20, 15.5);
        String expected = s.toString(); 
        // On vérifie juste que le toString ne renvoie pas null et contient les infos
        assertNotNull(expected);
        assertTrue(expected.contains("Jean"));
        assertTrue(expected.contains("Dupont"));
    }
}