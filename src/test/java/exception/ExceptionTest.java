package exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {
    @Test
    void testValidationException() {
        String message = "Erreur de test";
        ValidationException e = new ValidationException(message);
        assertEquals(message, e.getMessage());
    }
}