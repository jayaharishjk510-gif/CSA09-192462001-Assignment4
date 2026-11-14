package task03;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Campus Library Search Screen
 * Demonstrates nested layout architectures using BorderLayout, FlowLayout, and GridLayout.
 * Resizing guarantees the results table in BorderLayout.CENTER expands in both dimensions
 * while the search bar (NORTH) and button/status controls (SOUTH) maintain fixed height.
 */
public class CampusLibrarySearchScreen extends JFrame {
    private final JTextField txtKeyword = new JTextField(18);
    private final JComboBox<String> cmbCategory = new JComboBox<>(new String[]{
            "All Categories", "Computer Science", "Information Technology",
            "Electrical & Electronics", "Mechanical Engineering", "Civil Engineering", "Mathematics"
    });
    private final JButton btnSearch = new JButton("Search");
    private final JButton btnPrev = new JButton("<< Previous");
    private final JButton btnNext = new JButton("Next >>");
    private final JButton btnReserve = new JButton("Reserve Selected Book");
    private final JLabel lblStatus = new JLabel(" Ready. Enter search criteria above.", JLabel.LEFT);

    private final DefaultTableModel tableModel;
    private final JTable tblResults;

    public CampusLibrarySearchScreen() {
        super("SIMATS Engineering - Central Campus Library Catalog");
        tableModel = new DefaultTableModel(
                new Object[][]{
                        {"CS-101", "Introduction to Java Programming", "Y. Daniel Liang", "Pearson", "Available (8 copies)"},
                        {"CS-204", "Core Java Volume I: Fundamentals", "Cay S. Horstmann", "Oracle Press", "Available (3 copies)"},
                        {"CS-308", "Design Patterns: Elements of Reusable OO Software", "Gang of Four", "Addison-Wesley", "Reserved (0 copies)"},
                        {"IT-112", "Effective Java (3rd Edition)", "Joshua Bloch", "Addison-Wesley", "Available (5 copies)"},
                        {"IT-215", "Java: The Complete Reference", "Herbert Schildt", "McGraw-Hill", "Available (12 copies)"},
                        {"CS-401", "Database System Concepts", "Silberschatz, Korth", "McGraw-Hill", "Available (4 copies)"},
                        {"MA-201", "Higher Engineering Mathematics", "B.S. Grewal", "Khanna Publishers", "Available (15 copies)"},
                        {"EE-105", "Electric Circuits Fundamentals", "Alexander, Sadiku", "McGraw-Hill", "Available (2 copies)"}
                },
                new String[]{"Book ID", "Title", "Author(s)", "Publisher", "Availability Status"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only view
            }
        };

        tblResults = new JTable(tableModel);
        initLayout();
        registerListeners();
    }

    private void initLayout() {
        // Main window uses BorderLayout
        setLayout(new BorderLayout(0, 0));

        // ----------------- NORTH CONTAINER -----------------
        JPanel pnlNorth = new JPanel(new BorderLayout());

        // Title Bar Panel
        JPanel pnlTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        pnlTitle.setBackground(new Color(28, 54, 84));
        JLabel lblTitle = new JLabel("CAMPUS LIBRARY CATALOG SEARCH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(Color.WHITE);
        pnlTitle.add(lblTitle);

        // Search Row Panel (FlowLayout for natural component sizing)
        JPanel pnlSearchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        pnlSearchRow.setBackground(new Color(245, 247, 250));
        pnlSearchRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 215, 222)));

        JLabel lblSearch = new JLabel("Search Keyword:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel lblCat = new JLabel("Category:");
        lblCat.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnSearch.setBackground(new Color(33, 115, 70));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);

        pnlSearchRow.add(lblSearch);
        pnlSearchRow.add(txtKeyword);
        pnlSearchRow.add(lblCat);
        pnlSearchRow.add(cmbCategory);
        pnlSearchRow.add(btnSearch);

        pnlNorth.add(pnlTitle, BorderLayout.NORTH);
        pnlNorth.add(pnlSearchRow, BorderLayout.SOUTH);
        add(pnlNorth, BorderLayout.NORTH);

        // ----------------- CENTER CONTAINER (EXPANDS ON RESIZE) -----------------
        tblResults.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblResults.setRowHeight(24);
        tblResults.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblResults.getTableHeader().setBackground(new Color(230, 235, 245));
        tblResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tblResults);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);

        // ----------------- SOUTH CONTAINER -----------------
        JPanel pnlSouth = new JPanel(new BorderLayout());

        // Buttons Panel (FlowLayout centered)
        JPanel pnlActionButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 8));
        pnlActionButtons.setBackground(new Color(245, 247, 250));
        pnlActionButtons.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(210, 215, 222)));

        btnPrev.setEnabled(false);
        btnReserve.setBackground(new Color(180, 50, 40));
        btnReserve.setForeground(Color.WHITE);

        pnlActionButtons.add(btnPrev);
        pnlActionButtons.add(btnNext);
        pnlActionButtons.add(btnReserve);

        // Status Message Bar
        JPanel pnlStatusBar = new JPanel(new BorderLayout());
        pnlStatusBar.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        pnlStatusBar.setBackground(new Color(238, 240, 244));
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pnlStatusBar.add(lblStatus, BorderLayout.CENTER);

        pnlSouth.add(pnlActionButtons, BorderLayout.NORTH);
        pnlSouth.add(pnlStatusBar, BorderLayout.SOUTH);
        add(pnlSouth, BorderLayout.SOUTH);

        // Window properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 480);
        setMinimumSize(new Dimension(450, 320));
        setLocationRelativeTo(null);
    }

    private void registerListeners() {
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = txtKeyword.getText().trim();
                String category = (String) cmbCategory.getSelectedItem();
                lblStatus.setText(String.format(" Search completed for '%s' in [%s]. Displaying %d matching titles.",
                        query.isEmpty() ? "*" : query, category, tableModel.getRowCount()));
            }
        });

        btnReserve.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblResults.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(CampusLibrarySearchScreen.this,
                            "Please select a book from the table to reserve.",
                            "No Selection", JOptionPane.WARNING_MESSAGE);
                } else {
                    String title = (String) tableModel.getValueAt(selectedRow, 1);
                    String bookId = (String) tableModel.getValueAt(selectedRow, 0);
                    JOptionPane.showMessageDialog(CampusLibrarySearchScreen.this,
                            "Reservation confirmed for:\n[" + bookId + "] " + title + "\nPlease collect within 24 hours.",
                            "Reservation Success", JOptionPane.INFORMATION_MESSAGE);
                    lblStatus.setText(" Book [" + bookId + "] reserved successfully.");
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CampusLibrarySearchScreen().setVisible(true));
    }
}

