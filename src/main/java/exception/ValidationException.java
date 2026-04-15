package exception;

// Cette classe permet de transporter nos messages d'erreurs personnalisés
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}