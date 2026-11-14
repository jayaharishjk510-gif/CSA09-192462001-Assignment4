package task06.ui;

import task06.model.Registration;
import task06.service.RegistrationService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 * Management Table Panel displaying all event registrations with live filtering,
 * CSV export capabilities, and asynchronous SwingWorker data fetching.
 */
public class RegistrationTablePanel extends JPanel {
    private final JComboBox<String> cmbEventFilter = new JComboBox<>(new String[]{
            "All Events",
            "Hackathon 2026",
            "AI & Robotics Expo",
            "Web Development Sprint",
            "IoT Design Challenge",
            "CAD Modeling Contest",
            "Bio-Informatics Symposium",
            "Code Debugging Championship"
    });

    private final JButton btnFilter = new JButton("Filter Registrations");
    private final JButton btnRefresh = new JButton("Refresh All");
    private final JButton btnExport = new JButton("Export to CSV");

    private final DefaultTableModel tableModel;
    private final JTable tblRegistrations;
    private final JLabel lblTableStatus = new JLabel(" Loading registrations...", JLabel.LEFT);
    private final JProgressBar progressBar = new JProgressBar();

    private final RegistrationService registrationService;
    private List<Registration> currentData;

    public RegistrationTablePanel(RegistrationService service) {
        this.registrationService = service;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Setup Table Model (Read-Only)
        String[] columnNames = {"Reg ID", "Student ID", "Student Name", "Email Address", "Department", "Event Registered", "Registered At"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblRegistrations = new JTable(tableModel);
        initUI();
        registerHandlers();
        loadRegistrations("All Events");
    }

    private void initUI() {
        // Top Filter Bar
        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        JPanel pnlFilterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        pnlFilterBar.setBackground(new Color(245, 248, 252));
        pnlFilterBar.setBorder(BorderFactory.createLineBorder(new Color(215, 225, 238), 1));

        JLabel lblFilter = new JLabel("Filter by Event:");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExport.setBackground(new Color(36, 123, 160));
        btnExport.setForeground(Color.WHITE);

        pnlFilterBar.add(lblFilter);
        pnlFilterBar.add(cmbEventFilter);
        pnlFilterBar.add(btnFilter);
        pnlFilterBar.add(btnRefresh);
        pnlFilterBar.add(btnExport);

        pnlTop.add(pnlFilterBar, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        // Center Table View
        tblRegistrations.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblRegistrations.setRowHeight(25);
        tblRegistrations.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblRegistrations.getTableHeader().setBackground(new Color(230, 238, 248));
        tblRegistrations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblRegistrations.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Center align ID column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblRegistrations.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblRegistrations.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblRegistrations.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblRegistrations.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblRegistrations.getColumnModel().getColumn(3).setPreferredWidth(180);
        tblRegistrations.getColumnModel().getColumn(4).setPreferredWidth(180);
        tblRegistrations.getColumnModel().getColumn(5).setPreferredWidth(150);
        tblRegistrations.getColumnModel().getColumn(6).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(tblRegistrations);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 225)));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Status Panel
        JPanel pnlBottom = new JPanel(new BorderLayout(5, 5));
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        lblTableStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pnlBottom.add(progressBar, BorderLayout.NORTH);
        pnlBottom.add(lblTableStatus, BorderLayout.SOUTH);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void registerHandlers() {
        btnFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) cmbEventFilter.getSelectedItem();
                loadRegistrations(selected);
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cmbEventFilter.setSelectedIndex(0);
                loadRegistrations("All Events");
            }
        });

        btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportData();
            }
        });
    }

    public void loadRegistrations(final String eventFilter) {
        btnFilter.setEnabled(false);
        btnRefresh.setEnabled(false);
        progressBar.setVisible(true);
        lblTableStatus.setText(" Querying database for [" + eventFilter + "]... Please wait.");
        lblTableStatus.setForeground(Color.BLUE);

        // Asynchronous SwingWorker keeping database I/O off the EDT
        SwingWorker<List<Registration>, Void> worker = new SwingWorker<List<Registration>, Void>() {
            @Override
            protected List<Registration> doInBackground() throws Exception {
                long start = System.currentTimeMillis();
                List<Registration> list;
                if (eventFilter == null || eventFilter.equalsIgnoreCase("All Events")) {
                    list = registrationService.fetchAllRegistrations();
                } else {
                    list = registrationService.fetchRegistrationsByEvent(eventFilter);
                }
                long elapsed = System.currentTimeMillis() - start;
                // Tiny pause to simulate query transmission if instantaneous
                if (elapsed < 200) {
                    Thread.sleep(150);
                }
                return list;
            }

            @Override
            protected void done() {
                btnFilter.setEnabled(true);
                btnRefresh.setEnabled(true);
                progressBar.setVisible(false);

                try {
                    currentData = get();
                    // Clear previous rows
                    tableModel.setRowCount(0);

                    for (Registration r : currentData) {
                        tableModel.addRow(new Object[]{
                                r.getRegistrationId(),
                                r.getStudentId(),
                                r.getStudentName(),
                                r.getEmail(),
                                r.getDepartment(),
                                r.getEventName(),
                                r.getRegistrationTime() != null ? r.getRegistrationTime().toString() : "N/A"
                        });
                    }

                    lblTableStatus.setText(String.format(" Filter: [%s] | Total Records: %d | Status: Up-to-date",
                            eventFilter, currentData.size()));
                    lblTableStatus.setForeground(new Color(0, 100, 0));
                } catch (Exception ex) {
                    lblTableStatus.setText(" Error retrieving data: " + ex.getMessage());
                    lblTableStatus.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(RegistrationTablePanel.this,
                            "Failed to load registrations from database: " + ex.getMessage(),
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void exportData() {
        if (currentData == null || currentData.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No records to export.", "Export Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Registrations to CSV");
        fileChooser.setSelectedFile(new File("event_registrations_export.csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".csv");
            }

            try {
                int count = registrationService.exportToCSV(fileToSave, currentData);
                JOptionPane.showMessageDialog(this,
                        String.format("Successfully exported %d record(s) to:\n%s", count, fileToSave.getAbsolutePath()),
                        "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                lblTableStatus.setText(" Exported " + count + " records to CSV successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to export CSV: " + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

