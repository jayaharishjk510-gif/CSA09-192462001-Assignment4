package task06.dao;

import task06.model.Registration;
import task06.service.DuplicateRegistrationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Data Access Object (DAO) for Event Registrations.
 * Executes parameterized SQL queries via JDBC PreparedStatement.
 * Supports transparent fallback to an in-memory repository if MySQL is offline.
 */
public class RegistrationDAO {

    // In-memory fallback repository for offline/demonstration mode
    private static final List<Registration> memoryStore = new CopyOnWriteArrayList<>();
    private static final AtomicInteger idGenerator = new AtomicInteger(100);

    static {
        // Pre-populate with initial demonstration records
        memoryStore.add(new Registration(1, "2024CS101", "Aditya Sharma", "aditya.sharma@simats.edu", "Computer Science & Engineering", "Hackathon 2026", new Timestamp(System.currentTimeMillis() - 86400000L)));
        memoryStore.add(new Registration(2, "2024CS102", "Pooja Venkatesh", "pooja.v@simats.edu", "Computer Science & Engineering", "AI & Robotics Expo", new Timestamp(System.currentTimeMillis() - 72000000L)));
        memoryStore.add(new Registration(3, "2024IT201", "Rahul Nair", "rahul.nair@simats.edu", "Information Technology", "Web Development Sprint", new Timestamp(System.currentTimeMillis() - 54000000L)));
        memoryStore.add(new Registration(4, "2024EC305", "Sneha Krishnan", "sneha.k@simats.edu", "Electronics & Communication", "IoT Design Challenge", new Timestamp(System.currentTimeMillis() - 40000000L)));
        memoryStore.add(new Registration(5, "2024ME402", "Vikramaditya Rao", "vikram.rao@simats.edu", "Mechanical Engineering", "CAD Modeling Contest", new Timestamp(System.currentTimeMillis() - 20000000L)));
        memoryStore.add(new Registration(6, "2024CS105", "Ananya Deshmukh", "ananya.d@simats.edu", "Computer Science & Engineering", "Hackathon 2026", new Timestamp(System.currentTimeMillis() - 10000000L)));
    }

    /**
     * Checks whether a student is already registered for an event.
     */
    public boolean isAlreadyRegistered(String studentId, String eventName) throws SQLException {
        if (DatabaseConnection.isMySQLAvailable()) {
            String sql = "SELECT COUNT(*) FROM event_registrations WHERE student_id = ? AND event_name = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, studentId.trim());
                pstmt.setString(2, eventName.trim());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } else {
            for (Registration reg : memoryStore) {
                if (reg.getStudentId().equalsIgnoreCase(studentId.trim()) &&
                    reg.getEventName().equalsIgnoreCase(eventName.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Inserts a new registration record into the database using PreparedStatement.
     */
    public boolean registerStudent(Registration reg) throws SQLException, DuplicateRegistrationException {
        // First check duplicate
        if (isAlreadyRegistered(reg.getStudentId(), reg.getEventName())) {
            throw new DuplicateRegistrationException(String.format(
                    "Student ID '%s' is already registered for event '%s'. Duplicate registration is strictly prohibited.",
                    reg.getStudentId(), reg.getEventName()));
        }

        if (DatabaseConnection.isMySQLAvailable()) {
            String sql = "INSERT INTO event_registrations (student_id, student_name, email, department, event_name) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, reg.getStudentId().trim().toUpperCase());
                pstmt.setString(2, reg.getStudentName().trim());
                pstmt.setString(3, reg.getEmail().trim().toLowerCase());
                pstmt.setString(4, reg.getDepartment().trim());
                pstmt.setString(5, reg.getEventName().trim());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            reg.setRegistrationId(generatedKeys.getInt(1));
                        }
                    }
                    reg.setRegistrationTime(new Timestamp(System.currentTimeMillis()));
                    return true;
                }
            } catch (SQLException ex) {
                // MySQL Error Code 1062 = Duplicate entry for key 'uk_student_event'
                if (ex.getErrorCode() == 1062 || (ex.getSQLState() != null && ex.getSQLState().startsWith("23"))) {
                    throw new DuplicateRegistrationException("Database constraint violation: Student already registered for this event.");
                }
                throw ex;
            }
            return false;
        } else {
            // Memory storage insert
            int newId = idGenerator.incrementAndGet();
            reg.setRegistrationId(newId);
            reg.setRegistrationTime(new Timestamp(System.currentTimeMillis()));
            memoryStore.add(0, reg); // add at head for newest first
            return true;
        }
    }

    /**
     * Retrieves all registrations ordered by registration_id descending.
     */
    public List<Registration> getAllRegistrations() throws SQLException {
        List<Registration> list = new ArrayList<>();
        if (DatabaseConnection.isMySQLAvailable()) {
            String sql = "SELECT registration_id, student_id, student_name, email, department, event_name, registration_time " +
                         "FROM event_registrations ORDER BY registration_id DESC";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractRegistration(rs));
                }
            }
        } else {
            list.addAll(memoryStore);
        }
        return list;
    }

    /**
     * Filters registrations by specific event name.
     */
    public List<Registration> getRegistrationsByEvent(String eventName) throws SQLException {
        if (eventName == null || eventName.equalsIgnoreCase("All Events")) {
            return getAllRegistrations();
        }

        List<Registration> list = new ArrayList<>();
        if (DatabaseConnection.isMySQLAvailable()) {
            String sql = "SELECT registration_id, student_id, student_name, email, department, event_name, registration_time " +
                         "FROM event_registrations WHERE event_name = ? ORDER BY registration_id DESC";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, eventName.trim());
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        list.add(extractRegistration(rs));
                    }
                }
            }
        } else {
            for (Registration reg : memoryStore) {
                if (reg.getEventName().equalsIgnoreCase(eventName.trim())) {
                    list.add(reg);
                }
            }
        }
        return list;
    }

    /**
     * Alternative Concept: Paginated query using SQL LIMIT and OFFSET.
     */
    public List<Registration> getPaginatedRegistrations(String eventName, int offset, int limit) throws SQLException {
        List<Registration> list = new ArrayList<>();
        boolean hasFilter = eventName != null && !eventName.equalsIgnoreCase("All Events");

        if (DatabaseConnection.isMySQLAvailable()) {
            String sql = hasFilter
                    ? "SELECT registration_id, student_id, student_name, email, department, event_name, registration_time " +
                      "FROM event_registrations WHERE event_name = ? ORDER BY registration_id DESC LIMIT ? OFFSET ?"
                    : "SELECT registration_id, student_id, student_name, email, department, event_name, registration_time " +
                      "FROM event_registrations ORDER BY registration_id DESC LIMIT ? OFFSET ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                if (hasFilter) {
                    pstmt.setString(1, eventName.trim());
                    pstmt.setInt(2, limit);
                    pstmt.setInt(3, offset);
                } else {
                    pstmt.setInt(1, limit);
                    pstmt.setInt(2, offset);
                }
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        list.add(extractRegistration(rs));
                    }
                }
            }
        } else {
            List<Registration> base = hasFilter ? getRegistrationsByEvent(eventName) : memoryStore;
            int fromIndex = Math.min(offset, base.size());
            int toIndex = Math.min(offset + limit, base.size());
            list.addAll(base.subList(fromIndex, toIndex));
        }
        return list;
    }

    private Registration extractRegistration(ResultSet rs) throws SQLException {
        return new Registration(
                rs.getInt("registration_id"),
                rs.getString("student_id"),
                rs.getString("student_name"),
                rs.getString("email"),
                rs.getString("department"),
                rs.getString("event_name"),
                rs.getTimestamp("registration_time")
        );
    }
}

