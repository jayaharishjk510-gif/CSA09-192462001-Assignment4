package task06.dao;

/**
 * Encapsulates MySQL JDBC configuration and connection strings.
 */
public class DatabaseConfig {
    public static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String DB_URL = "jdbc:mysql://localhost:3306/campus_event_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "root"; // adjust to local environment password
}

