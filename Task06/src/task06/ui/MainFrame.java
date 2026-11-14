package task06.ui;

import task06.dao.DatabaseConnection;
import task06.service.RegistrationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Main Application Window integrating JMenuBar, Tabbed Navigation,
 * Registration Form Panel, and Management Table Panel.
 */
public class MainFrame extends JFrame {
    private final RegistrationService registrationService;
    private final RegistrationFormPanel formPanel;
    private final RegistrationTablePanel tablePanel;
    private final JTabbedPane tabbedPane;

    public MainFrame() {
        super("SIMATS Engineering - Campus Event Registration System (CO4 Assignment 4)");
        this.registrationService = new RegistrationService();

        // Initialize panels
        this.tablePanel = new RegistrationTablePanel(registrationService);
        this.formPanel = new RegistrationFormPanel(registrationService, new Runnable() {
            @Override
            public void run() {
                // When a new registration succeeds, automatically refresh table
                tablePanel.loadRegistrations("All Events");
            }
        });

        this.tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.addTab("  Student Registration Form  ", formPanel);
        tabbedPane.addTab("  Event Registrations Manager  ", tablePanel);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setJMenuBar(createMenuBar());
        add(tabbedPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 620);
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(null);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // 1. File Menu
        JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);

        JMenuItem itemNew = new JMenuItem("New Registration");
        itemNew.setMnemonic(KeyEvent.VK_N);
        itemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        itemNew.addActionListener(e -> {
            tabbedPane.setSelectedIndex(0);
            formPanel.clearFields();
        });

        JMenuItem itemRefresh = new JMenuItem("Refresh Registrations");
        itemRefresh.setMnemonic(KeyEvent.VK_R);
        itemRefresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        itemRefresh.addActionListener(e -> tablePanel.loadRegistrations("All Events"));

        JMenuItem itemExit = new JMenuItem("Exit Application");
        itemExit.setMnemonic(KeyEvent.VK_X);
        itemExit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    MainFrame.this,
                    "Are you sure you want to exit the application?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0);
            }
        });

        menuFile.add(itemNew);
        menuFile.add(itemRefresh);
        menuFile.addSeparator();
        menuFile.add(itemExit);

        // 2. View Menu
        JMenu menuView = new JMenu("View");
        menuView.setMnemonic(KeyEvent.VK_V);

        JMenuItem itemFormView = new JMenuItem("Show Registration Form");
        itemFormView.addActionListener(e -> tabbedPane.setSelectedIndex(0));

        JMenuItem itemTableView = new JMenuItem("Show Registrations Table");
        itemTableView.addActionListener(e -> tabbedPane.setSelectedIndex(1));

        menuView.add(itemFormView);
        menuView.add(itemTableView);

        // 3. Help Menu
        JMenu menuHelp = new JMenu("Help");
        menuHelp.setMnemonic(KeyEvent.VK_H);

        JMenuItem itemDbStatus = new JMenuItem("Database Status");
        itemDbStatus.addActionListener(e -> {
            boolean isLive = DatabaseConnection.isMySQLAvailable();
            String status = isLive
                    ? "Connected to Live MySQL Database (campus_event_db) at localhost:3306"
                    : "Operating in Fallback Mode (In-Memory Thread-Safe Data Store). MySQL server not reachable.";
            JOptionPane.showMessageDialog(MainFrame.this, status, "Database Connection Status",
                    isLive ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        });

        JMenuItem itemAbout = new JMenuItem("About Assignment");
        itemAbout.addActionListener(e -> {
            String about = "SIMATS Engineering - Programming in Java (CSA09)\n" +
                    "Assignment 4 (CO4): Event-Driven GUI Design\n" +
                    "Task 6: Campus Event Registration Desktop Application\n\n" +
                    "Architecture: Layered MVC (Swing GUI -> Service -> JDBC DAO -> MySQL)\n" +
                    "Features: Asynchronous SwingWorker, PreparedStatement, Duplicate Prevention, CSV Export.";
            JOptionPane.showMessageDialog(MainFrame.this, about, "About Application", JOptionPane.INFORMATION_MESSAGE);
        });

        menuHelp.add(itemDbStatus);
        menuHelp.addSeparator();
        menuHelp.add(itemAbout);

        menuBar.add(menuFile);
        menuBar.add(menuView);
        menuBar.add(menuHelp);

        return menuBar;
    }
}
