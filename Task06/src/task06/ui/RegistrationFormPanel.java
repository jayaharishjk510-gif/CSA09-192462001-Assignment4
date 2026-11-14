package task06.ui;

import task06.model.Registration;
import task06.service.DuplicateRegistrationException;
import task06.service.RegistrationService;
import task06.service.ValidationException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Registration Form Panel for entering student and event details.
 * Uses SwingWorker to perform registration asynchronously off the EDT.
 */
public class RegistrationFormPanel extends JPanel {
    private final JTextField txtStudentId = new JTextField(20);
    private final JTextField txtStudentName = new JTextField(20);
    private final JTextField txtEmail = new JTextField(20);
    private final JComboBox<String> cmbDepartment = new JComboBox<>(new String[]{
            "-- Select Department --",
            "Computer Science & Engineering",
            "Information Technology",
            "Electronics & Communication",
            "Mechanical Engineering",
            "Biotechnology",
            "Civil Engineering",
            "Artificial Intelligence & Data Science"
    });
    private final JComboBox<String> cmbEvent = new JComboBox<>(new String[]{
            "-- Select Event --",
            "Hackathon 2026",
            "AI & Robotics Expo",
            "Web Development Sprint",
            "IoT Design Challenge",
            "CAD Modeling Contest",
            "Bio-Informatics Symposium",
            "Code Debugging Championship"
    });

    private final JButton btnRegister = new JButton("Register for Event");
    private final JButton btnClear = new JButton("Clear Form");
    private final JLabel lblStatus = new JLabel("Enter student details and click Register.", JLabel.CENTER);
    private final JProgressBar progressBar = new JProgressBar();

    private final RegistrationService registrationService;
    private final Runnable onRegistrationSuccessCallback;

    public RegistrationFormPanel(RegistrationService service, Runnable onSuccessCallback) {
        this.registrationService = service;
        this.onRegistrationSuccessCallback = onSuccessCallback;

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        initUI();
        registerHandlers();
    }

    private void initUI() {
        // Banner
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 4, 4));
        JLabel lblHeader = new JLabel("Campus Cultural & Technical Event Registration");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(24, 60, 110));
        JLabel lblSub = new JLabel("Please fill in accurate details. Duplicate registrations for the same event are rejected.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Color.DARK_GRAY);
        pnlHeader.add(lblHeader);
        pnlHeader.add(lblSub);
        add(pnlHeader, BorderLayout.NORTH);

        // Form Fields in GridBagLayout
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Registration Details"),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Student ID
        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(createBoldLabel("Student ID / Reg No:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(txtStudentId, gbc);

        // Row 1: Student Name
        gbc.gridx = 0; gbc.gridy = 1;
        pnlForm.add(createBoldLabel("Student Full Name:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(txtStudentName, gbc);

        // Row 2: Email
        gbc.gridx = 0; gbc.gridy = 2;
        pnlForm.add(createBoldLabel("Official College Email:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(txtEmail, gbc);

        // Row 3: Department
        gbc.gridx = 0; gbc.gridy = 3;
        pnlForm.add(createBoldLabel("Academic Department:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(cmbDepartment, gbc);

        // Row 4: Event
        gbc.gridx = 0; gbc.gridy = 4;
        pnlForm.add(createBoldLabel("Select Event:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(cmbEvent, gbc);

        // Row 5: Action Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnRegister.setBackground(new Color(30, 130, 76));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegister.setFocusPainted(false);

        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        pnlButtons.add(btnRegister);
        pnlButtons.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        pnlForm.add(pnlButtons, gbc);

        add(pnlForm, BorderLayout.CENTER);

        // South Panel: Status and Progress Bar
        JPanel pnlSouth = new JPanel(new BorderLayout(5, 5));
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pnlSouth.add(progressBar, BorderLayout.NORTH);
        pnlSouth.add(lblStatus, BorderLayout.SOUTH);
        add(pnlSouth, BorderLayout.SOUTH);
    }

    private JLabel createBoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return label;
    }

    private void registerHandlers() {
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeRegistration();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });
    }

    public void clearFields() {
        txtStudentId.setText("");
        txtStudentName.setText("");
        txtEmail.setText("");
        cmbDepartment.setSelectedIndex(0);
        cmbEvent.setSelectedIndex(0);
        lblStatus.setText("Form cleared. Ready for input.");
        lblStatus.setForeground(Color.DARK_GRAY);
        txtStudentId.requestFocus();
    }

    private void executeRegistration() {
        final String studentId = txtStudentId.getText().trim();
        final String name = txtStudentName.getText().trim();
        final String email = txtEmail.getText().trim();
        final String dept = (String) cmbDepartment.getSelectedItem();
        final String event = (String) cmbEvent.getSelectedItem();

        // 1. Client-side Pre-validation check
        try {
            registrationService.validateRegistration(studentId, name, email, dept, event);
        } catch (ValidationException ex) {
            lblStatus.setText("Validation Error: " + ex.getMessage());
            lblStatus.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Lock UI & show progress indicator
        btnRegister.setEnabled(false);
        progressBar.setVisible(true);
        lblStatus.setText("Registering student... Contacting database.");
        lblStatus.setForeground(Color.BLUE);

        // 3. Keep long-running database work off the EDT using SwingWorker
        SwingWorker<Registration, Void> worker = new SwingWorker<Registration, Void>() {
            @Override
            protected Registration doInBackground() throws Exception {
                // Background thread handles business checks and JDBC execution
                return registrationService.registerStudent(studentId, name, email, dept, event);
            }

            @Override
            protected void done() {
                btnRegister.setEnabled(true);
                progressBar.setVisible(false);

                try {
                    Registration reg = get();
                    lblStatus.setText("SUCCESS: Registered " + reg.getStudentName() + " for " + reg.getEventName());
                    lblStatus.setForeground(new Color(0, 128, 0));

                    JOptionPane.showMessageDialog(
                            RegistrationFormPanel.this,
                            String.format("Registration Successful!\n\nID: %d\nStudent: %s (%s)\nEvent: %s\nDept: %s",
                                    reg.getRegistrationId(), reg.getStudentName(), reg.getStudentId(),
                                    reg.getEventName(), reg.getDepartment()),
                            "Registration Confirmed",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    clearFields();
                    if (onRegistrationSuccessCallback != null) {
                        onRegistrationSuccessCallback.run();
                    }
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    if (cause instanceof DuplicateRegistrationException) {
                        lblStatus.setText("REJECTED: Duplicate registration detected.");
                        lblStatus.setForeground(Color.RED);
                        JOptionPane.showMessageDialog(
                                RegistrationFormPanel.this,
                                cause.getMessage(),
                                "Duplicate Registration Prohibited",
                                JOptionPane.WARNING_MESSAGE
                        );
                    } else if (cause instanceof ValidationException) {
                        lblStatus.setText("Validation Error: " + cause.getMessage());
                        lblStatus.setForeground(Color.RED);
                        JOptionPane.showMessageDialog(
                                RegistrationFormPanel.this,
                                cause.getMessage(),
                                "Validation Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    } else {
                        lblStatus.setText("Database Error: " + cause.getMessage());
                        lblStatus.setForeground(Color.RED);
                        JOptionPane.showMessageDialog(
                                RegistrationFormPanel.this,
                                "Database operation failed: " + cause.getMessage(),
                                "System Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        };

        worker.execute();
    }
}
