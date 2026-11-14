package task06.service;

/**
 * Thrown when a student attempts to register for an event they are already registered for.
 */
public class DuplicateRegistrationException extends Exception {
    public DuplicateRegistrationException(String message) {
        super(message);
    }
}

