package service;

import dao.StudentDAO;
import exception.ValidationException;
import model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import java.util.List;

class StudentServiceTest {

    @Mock
    private StudentDAO studentDAO;

    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- TESTS : validateAndAdd ---

    @Test
    void testValidateAndAdd_Success() throws ValidationException {
        when(studentDAO.addStudent(any(Student.class))).thenReturn(true);
        studentService.validateAndAdd("jean", "dupont", "25", "15.0");
        verify(studentDAO, times(1)).addStudent(any(Student.class));
    }

    @Test
    void testValidateAndAdd_AgeTropBas() {
        assertThrows(ValidationException.class, () -> 
            studentService.validateAndAdd("Jean", "Dupont", "15", "10.0")
        );
    }

    @Test
    void testValidateAndAdd_NoteInvalide() {
        assertThrows(ValidationException.class, () -> 
            studentService.validateAndAdd("Jean", "Dupont", "25", "22.0")
        );
    }

    @Test
    void testValidateAndAdd_NomContientChiffres() {
        ValidationException exception = assertThrows(ValidationException.class, () -> 
            studentService.validateAndAdd("Jean123", "Dupont", "25", "10.0")
        );
        assertTrue(exception.getMessage().contains("doivent contenir que des lettres"));
    }

    // NOUVEAU : Test pour couvrir le catch du parsing d'âge (100% coverage)
    @Test
    void testValidateAndAdd_AgeNonNumerique() {
        assertThrows(ValidationException.class, () -> 
            studentService.validateAndAdd("Jean", "Dupont", "vingt ans", "10.0")
        );
    }

    // NOUVEAU : Test pour le cas où le DAO renvoie 'false' (échec BDD)
    @Test
    void testValidateAndAdd_DatabaseError() {
        when(studentDAO.addStudent(any(Student.class))).thenReturn(false);
        assertThrows(ValidationException.class, () -> 
            studentService.validateAndAdd("Jean", "Dupont", "25", "10.0")
        );
    }

    // --- TESTS : Statistiques ---

    @Test
    void testGetFormattedStats_ListeVideOuNull() {
        // Test liste vide
        assertEquals("Aucun résultat trouvé pour ces critères.", 
            studentService.getFormattedStatsForSelection(java.util.Collections.emptyList()));
        // Test liste null (pour couvrir le if (students == null))
        assertEquals("Aucun résultat trouvé pour ces critères.", 
            studentService.getFormattedStatsForSelection(null));
    }

    @Test
    void testGetFormattedStats_AvecEtudiants() {
        List<Student> students = List.of(
            new Student(1, "A", "B", 20, 10.0),
            new Student(2, "C", "D", 22, 14.0)
        );
        String result = studentService.getFormattedStatsForSelection(students);
        assertTrue(result.contains("12,00") || result.contains("12.00"));
        assertTrue(result.contains("2 élèves"));
    }

    @Test
    void testGetGlobalStatsString() {
        when(studentDAO.getGlobalStats()).thenReturn("Stats bidon");
        assertEquals("Stats bidon", studentService.getGlobalStatsString());
    }

    // --- TESTS : Mise à jour et Suppression ---

    @Test
    void testValidateAndUpdate_Success() throws ValidationException {
        Student s = new Student(1, "Ancien", "Nom", 20, 10.0);
        studentService.validateAndUpdate(s, "Nouveau", "Nom", "25", "18.5");
        assertEquals("Nouveau", s.getFirstName());
        assertEquals(18.5, s.getGrade());
        verify(studentDAO, times(1)).updateStudent(s);
    }

    // NOUVEAU : Test pour le catch de validateAndUpdate (Note invalide)
    @Test
    void testValidateAndUpdate_Invalide() {
        Student s = new Student(1, "A", "B", 20, 10.0);
        assertThrows(ValidationException.class, () -> 
            studentService.validateAndUpdate(s, "A", "B", "20", "50.0") // Note > 20
        );
    }

    @Test
    void testDeleteStudent() {
        studentService.deleteStudent(1);
        verify(studentDAO, times(1)).deleteMultiple(anyList());
    }

    @Test
    void testDeleteMultipleStudents() {
        List<Integer> ids = List.of(1, 2, 3);
        studentService.deleteMultipleStudents(ids);
        verify(studentDAO).deleteMultiple(ids);
    }
    @Test
    void testConstructor() {
        assertNotNull(new StudentService());
    }
    @Test
    void testValidateAndAdd_NullValues() {
        assertThrows(ValidationException.class, () -> 
            studentService.validateAndAdd(null, null, "20", "10")
        );
    }
}