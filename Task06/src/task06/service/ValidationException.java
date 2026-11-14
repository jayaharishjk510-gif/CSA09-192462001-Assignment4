package task06.service;

/**
 * Thrown when user input fails client or service-level validation checks.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
