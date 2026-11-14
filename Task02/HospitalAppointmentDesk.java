package task02;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Hospital Outpatient Appointment Desk GUI Implementation
 * Demonstrates dynamic event handling, cascading dropdowns,
 * asynchronous background booking, and window closing confirmation.
 */
public class HospitalAppointmentDesk extends JFrame {
    private final JTextField txtPatientId = new JTextField(15);
    private final JComboBox<String> cmbDepartment = new JComboBox<>();
    private final JComboBox<String> cmbDoctor = new JComboBox<>();
    private final JComboBox<String> cmbTimeSlot = new JComboBox<>();
    private final JButton btnBook = new JButton("Book Appointment");
    private final JButton btnReset = new JButton("Reset Form");
    private final JLabel lblStatus = new JLabel("Please enter patient details and choose department.", JLabel.CENTER);

    // In-memory cache: Map<Department, List<Doctor>>
    private final Map<String, List<String>> departmentDoctorMap = new LinkedHashMap<>();

    // State flag tracking whether a booking operation is currently active
    private volatile boolean isBookingInProgress = false;

    public HospitalAppointmentDesk() {
        super("SIMATS Health Center - Outpatient Appointment Desk");
        initDataCache();
        initUI();
        registerEventHandlers();
    }

    private void initDataCache() {
        departmentDoctorMap.put("Cardiology", Arrays.asList("Dr. A. Sharma (MD, DM)", "Dr. R. Venkatesh (FRCP)", "Dr. S. Nair (FACC)"));
        departmentDoctorMap.put("Neurology", Arrays.asList("Dr. K. Mehta (MCh)", "Dr. P. Sen (DNB, DM)", "Dr. T. Jayaraman (MD, PhD)"));
        departmentDoctorMap.put("Orthopedics", Arrays.asList("Dr. M. Balaji (MS Ortho)", "Dr. V. Raman (D.Ortho)", "Dr. C. Andrews (MCh)"));
        departmentDoctorMap.put("Pediatrics", Arrays.asList("Dr. N. Gupta (MD Peds)", "Dr. L. Fernandez (DCH)", "Dr. H. Swaminathan (MD)"));
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));

        // Header Panel
        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(new Color(24, 76, 120));
        JLabel lblTitle = new JLabel("HOSPITAL OUTPATIENT APPOINTMENT DESK");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // Center Form Panel
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Populate Department
        cmbDepartment.addItem("-- Select Department --");
        for (String dept : departmentDoctorMap.keySet()) {
            cmbDepartment.addItem(dept);
        }

        // Initialize Doctor and Time Slots
        cmbDoctor.addItem("-- Select Department First --");
        cmbDoctor.setEnabled(false);

        cmbTimeSlot.addItem("09:00 AM - 09:30 AM");
        cmbTimeSlot.addItem("10:00 AM - 10:30 AM");
        cmbTimeSlot.addItem("11:30 AM - 12:00 PM");
        cmbTimeSlot.addItem("02:00 PM - 02:30 PM");
        cmbTimeSlot.addItem("03:30 PM - 04:00 PM");
        cmbTimeSlot.addItem("04:30 PM - 05:00 PM");

        // Row 0: Patient ID
        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(new JLabel("Patient ID:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(txtPatientId, gbc);

        // Row 1: Department
        gbc.gridx = 0; gbc.gridy = 1;
        pnlForm.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(cmbDepartment, gbc);

        // Row 2: Doctor
        gbc.gridx = 0; gbc.gridy = 2;
        pnlForm.add(new JLabel("Consulting Doctor:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(cmbDoctor, gbc);

        // Row 3: Time Slot
        gbc.gridx = 0; gbc.gridy = 3;
        pnlForm.add(new JLabel("Appointment Slot:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(cmbTimeSlot, gbc);

        // Button Panel
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnlButtons.add(btnBook);
        pnlButtons.add(btnReset);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        pnlForm.add(pnlButtons, gbc);

        add(pnlForm, BorderLayout.CENTER);

        // Status Panel
        JPanel pnlStatus = new JPanel(new BorderLayout());
        pnlStatus.setBorder(BorderFactory.createEmptyBorder(6, 10, 8, 10));
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(40, 40, 40));
        pnlStatus.add(lblStatus, BorderLayout.CENTER);
        add(pnlStatus, BorderLayout.SOUTH);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(520, 380);
        setLocationRelativeTo(null);
    }

    private void registerEventHandlers() {
        // Event 1: Department change -> Refresh Doctor dropdown
        cmbDepartment.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    refreshDoctorList((String) cmbDepartment.getSelectedItem());
                }
            }
        });

        // Event 2: Book Button Action
        btnBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookAppointment();
            }
        });

        // Event 3: Reset Button Action
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });

        // Event 4: Window Closing Confirmation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
    }

    private void refreshDoctorList(String selectedDept) {
        cmbDoctor.removeAllItems();
        if (selectedDept == null || selectedDept.startsWith("--")) {
            cmbDoctor.addItem("-- Select Department First --");
            cmbDoctor.setEnabled(false);
            lblStatus.setText("Select a valid medical department.");
            lblStatus.setForeground(Color.DARK_GRAY);
        } else {
            List<String> doctors = departmentDoctorMap.get(selectedDept);
            if (doctors != null && !doctors.isEmpty()) {
                for (String doc : doctors) {
                    cmbDoctor.addItem(doc);
                }
                cmbDoctor.setEnabled(true);
                lblStatus.setText(doctors.size() + " specialist(s) available in " + selectedDept + ".");
                lblStatus.setForeground(new Color(0, 100, 0));
            }
        }
    }

    private void bookAppointment() {
        String patientId = txtPatientId.getText().trim();
        String dept = (String) cmbDepartment.getSelectedItem();
        String doctor = (String) cmbDoctor.getSelectedItem();
        String slot = (String) cmbTimeSlot.getSelectedItem();

        // Validation
        if (patientId.isEmpty()) {
            lblStatus.setText("Validation Error: Patient ID is required.");
            lblStatus.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "Please enter a valid Patient ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
            txtPatientId.requestFocus();
            return;
        }

        if (dept == null || dept.startsWith("--")) {
            lblStatus.setText("Validation Error: Please select a department.");
            lblStatus.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "Please select an outpatient department.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (doctor == null || doctor.startsWith("--")) {
            lblStatus.setText("Validation Error: Please select a consulting doctor.");
            lblStatus.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "Please select a doctor.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Set booking in progress flag
        isBookingInProgress = true;
        btnBook.setEnabled(false);
        lblStatus.setText("Booking in progress... Synchronizing with appointment server.");
        lblStatus.setForeground(Color.BLUE);

        // Simulate asynchronous booking via SwingWorker
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                // Simulate network latency / database write
                Thread.sleep(1800);
                return "APT-" + (10000 + new Random().nextInt(90000));
            }

            @Override
            protected void done() {
                try {
                    String appointmentRef = get();
                    isBookingInProgress = false;
                    btnBook.setEnabled(true);

                    lblStatus.setText("SUCCESS: Booked! Token Ref: " + appointmentRef);
                    lblStatus.setForeground(new Color(0, 128, 0));

                    String msg = String.format("Appointment Confirmed Successfully!\n\nReference: %s\nPatient ID: %s\nDepartment: %s\nDoctor: %s\nSlot: %s",
                            appointmentRef, patientId, dept, doctor, slot);
                    JOptionPane.showMessageDialog(HospitalAppointmentDesk.this, msg, "Booking Confirmation", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    isBookingInProgress = false;
                    btnBook.setEnabled(true);
                    lblStatus.setText("Error: Booking failed. " + ex.getMessage());
                    lblStatus.setForeground(Color.RED);
                }
            }
        };
        worker.execute();
    }

    private void resetForm() {
        if (isBookingInProgress) {
            JOptionPane.showMessageDialog(this, "Cannot reset form while booking is in progress.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        txtPatientId.setText("");
        cmbDepartment.setSelectedIndex(0);
        cmbDoctor.removeAllItems();
        cmbDoctor.addItem("-- Select Department First --");
        cmbDoctor.setEnabled(false);
        cmbTimeSlot.setSelectedIndex(0);
        lblStatus.setText("Form reset. Ready for new appointment.");
        lblStatus.setForeground(Color.DARK_GRAY);
        txtPatientId.requestFocus();
    }

    private void handleWindowClosing() {
        if (isBookingInProgress) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "A booking is currently in progress!\nAre you sure you want to abort the transaction and exit?",
                    "Confirm Application Exit",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0);
            }
        } else {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to close the Outpatient Desk?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HospitalAppointmentDesk().setVisible(true));
    }
}

