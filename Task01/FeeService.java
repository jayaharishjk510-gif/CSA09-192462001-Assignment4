package task01;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Service class responsible for fee validation, business rules,
 * and secure database transactions. Completely decoupled from GUI.
 */
public class FeeService {
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    public FeeService(String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    public static class PaymentReceipt {
        private final String regNo;
        private final double amount;
        private final String transactionId;
        private final String timestamp;

        public PaymentReceipt(String regNo, double amount, String transactionId, String timestamp) {
            this.regNo = regNo;
            this.amount = amount;
            this.transactionId = transactionId;
            this.timestamp = timestamp;
        }

        public String getRegNo() { return regNo; }
        public double getAmount() { return amount; }
        public String getTransactionId() { return transactionId; }
        public String getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return String.format("Receipt ID: %s\nReg No: %s\nAmount Paid: Rs. %.2f\nDate/Time: %s\nStatus: SUCCESS",
                    transactionId, regNo, amount, timestamp);
        }
    }

    /**
     * Validates student registration number and payment amount.
     */
    public double validate(String regNo, String amountStr) throws ValidationException {
        if (regNo == null || regNo.trim().isEmpty()) {
            throw new ValidationException("Registration Number cannot be empty.");
        }
        if (!regNo.trim().matches("^[A-Za-z0-9]+$")) {
            throw new ValidationException("Registration Number must be alphanumeric.");
        }
        if (amountStr == null || amountStr.trim().isEmpty()) {
            throw new ValidationException("Fee amount cannot be empty.");
        }
        double amount;
        try {
            amount = Double.parseDouble(amountStr.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Amount must be a valid numeric figure.");
        }
        if (amount <= 0.0) {
            throw new ValidationException("Fee amount must be greater than zero.");
        }
        return amount;
    }

    /**
     * Processes payment securely using parameterized PreparedStatement
     * and manages connection resources safely.
     */
    public PaymentReceipt processPayment(String regNo, String amountStr) throws ValidationException, SQLException {
        double validAmount = validate(regNo, amountStr);

        // Secure database insert using PreparedStatement and try-with-resources
        String sql = "INSERT INTO fees (reg_no, amount, payment_time) VALUES (?, ?, CURRENT_TIMESTAMP)";
        
        // Attempt database persistence if credentials are configured
        if (dbUrl != null && !dbUrl.isEmpty()) {
            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, regNo.trim().toUpperCase());
                pstmt.setDouble(2, validAmount);
                pstmt.executeUpdate();
            }
        }

        // Simulate bank confirmation latency (designed to be executed off the EDT)
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String txnId = "TXN" + System.currentTimeMillis();
        String timeStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        return new PaymentReceipt(regNo.trim().toUpperCase(), validAmount, txnId, timeStr);
    }
}
