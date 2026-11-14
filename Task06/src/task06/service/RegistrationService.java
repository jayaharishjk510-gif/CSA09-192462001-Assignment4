package task06.service;

import task06.dao.RegistrationDAO;
import task06.model.Registration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service Layer coordinating business validations, duplicate checking,
 * persistence, and CSV export. Completely isolated from Swing UI.
 */
public class RegistrationService {
    private final RegistrationDAO registrationDAO;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern STUDENT_ID_PATTERN =
            Pattern.compile("^[A-Za-z0-9]{5,15}$");

    public RegistrationService() {
        this.registrationDAO = new RegistrationDAO();
    }

    public RegistrationService(RegistrationDAO registrationDAO) {
        this.registrationDAO = registrationDAO;
    }

    /**
     * Comprehensive business validation of all registration fields.
     */
    public void validateRegistration(String studentId, String studentName, String email, String department, String eventName)
            throws ValidationException {

        if (studentId == null || studentId.trim().isEmpty()) {
            throw new ValidationException("Student ID is mandatory.");
        }
        if (!STUDENT_ID_PATTERN.matcher(studentId.trim()).matches()) {
            throw new ValidationException("Student ID must be 5-15 alphanumeric characters (e.g., 2024CS101).");
        }

        if (studentName == null || studentName.trim().isEmpty()) {
            throw new ValidationException("Student Name is mandatory.");
        }
        if (studentName.trim().length() < 3 || !studentName.trim().matches("^[A-Za-z ]+$")) {
            throw new ValidationException("Student Name must contain at least 3 alphabetic characters.");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email address is mandatory.");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Please enter a valid email address (e.g., student@simats.edu).");
        }

        if (department == null || department.trim().startsWith("--")) {
            throw new ValidationException("Please select a valid academic department.");
        }

        if (eventName == null || eventName.trim().startsWith("--")) {
            throw new ValidationException("Please select a target cultural/technical event.");
        }
    }

    /**
     * Executes registration with validation and duplicate checking.
     */
    public Registration registerStudent(String studentId, String studentName, String email, String department, String eventName)
            throws ValidationException, DuplicateRegistrationException, SQLException {

        validateRegistration(studentId, studentName, email, department, eventName);

        Registration reg = new Registration(
                studentId.trim().toUpperCase(),
                studentName.trim(),
                email.trim().toLowerCase(),
                department.trim(),
                eventName.trim()
        );

        registrationDAO.registerStudent(reg);
        return reg;
    }

    public List<Registration> fetchAllRegistrations() throws SQLException {
        return registrationDAO.getAllRegistrations();
    }

    public List<Registration> fetchRegistrationsByEvent(String eventName) throws SQLException {
        return registrationDAO.getRegistrationsByEvent(eventName);
    }

    /**
     * Exports registrations to CSV file.
     */
    public int exportToCSV(File targetFile, List<Registration> data) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IOException("No registration data available to export.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile))) {
            // Write CSV Header
            writer.write("Registration_ID,Student_ID,Student_Name,Email,Department,Event_Name,Registration_Time");
            writer.newLine();

            for (Registration reg : data) {
                String line = String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                        reg.getRegistrationId(),
                        escapeCsv(reg.getStudentId()),
                        escapeCsv(reg.getStudentName()),
                        escapeCsv(reg.getEmail()),
                        escapeCsv(reg.getDepartment()),
                        escapeCsv(reg.getEventName()),
                        reg.getRegistrationTime() != null ? reg.getRegistrationTime().toString() : ""
                );
                writer.write(line);
                writer.newLine();
            }
        }
        return data.size();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
}
