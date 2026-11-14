package task06.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages database connectivity to MySQL.
 */
public class DatabaseConnection {
    private static boolean driverLoaded = false;
    private static boolean mysqlAvailable = false;
    private static boolean testedConnection = false;

    static {
        try {
            Class.forName(DatabaseConfig.JDBC_DRIVER);
            driverLoaded = true;
        } catch (ClassNotFoundException e) {
            System.err.println("[INFO] MySQL JDBC Driver not found in classpath. Falling back to internal persistence mode.");
            driverLoaded = false;
        }
    }

    public static synchronized boolean isMySQLAvailable() {
        if (!testedConnection) {
            testedConnection = true;
            if (driverLoaded) {
                try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD)) {
                    mysqlAvailable = true;
                    System.out.println("[INFO] Successfully connected to live MySQL Server: campus_event_db");
                } catch (SQLException e) {
                    System.out.println("[INFO] Live MySQL connection failed (" + e.getMessage() + "). Operating in resilient fallback mode.");
                    mysqlAvailable = false;
                }
            } else {
                mysqlAvailable = false;
            }
        }
        return mysqlAvailable;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
    }
}

