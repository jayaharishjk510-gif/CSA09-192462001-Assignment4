package task01;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

/**
 * Redesigned Fee Payment Window conforming to MVC / Layered architecture.
 * Handles UI display, user feedback, and event delegation.
 */
public class FeeWindow extends Frame {
    private final TextField txtRegNo = new TextField(20);
    private final TextField txtAmount = new TextField(20);
    private final Button btnPay = new Button("Pay");
    private final Button btnClear = new Button("Clear");
    private final Button btnReceipt = new Button("Receipt");
    private final Label lblStatus = new Label("Ready to accept payment.", Label.CENTER);

    private final FeeService feeService;
    private FeeService.PaymentReceipt lastReceipt = null;

    public FeeWindow(FeeService feeService) {
        super("SIMATS Engineering - Student Fee Payment");
        this.feeService = feeService;

        initUI();
        registerEventHandlers();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // Form Panel (Grid)
        Panel formPanel = new Panel(new GridLayout(2, 2, 10, 10));
        Label lblReg = new Label("Registration Number:");
        Label lblAmt = new Label("Fee Amount (Rs.):");
        lblReg.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblAmt.setFont(new Font("SansSerif", Font.BOLD, 12));

        formPanel.add(lblReg);
        formPanel.add(txtRegNo);
        formPanel.add(lblAmt);
        formPanel.add(txtAmount);

        // Button Panel (Flow)
        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(btnPay);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnReceipt);

        // Status Bar
        lblStatus.setBackground(new Color(230, 240, 250));
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Add padding
        Panel centerContainer = new Panel(new BorderLayout(10, 10));
        centerContainer.add(formPanel, BorderLayout.NORTH);
        centerContainer.add(buttonPanel, BorderLayout.CENTER);

        add(centerContainer, BorderLayout.CENTER);
        add(lblStatus, BorderLayout.SOUTH);

        setSize(420, 220);
        setLocationRelativeTo(null);
    }

    private void registerEventHandlers() {
        // Individual listener for Pay button (Non-blocking background thread)
        btnPay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePayment();
            }
        });

        // Individual listener for Clear button
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleClear();
            }
        });

        // Individual listener for Receipt button
        btnReceipt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleReceipt();
            }
        });

        // Window closing adapter
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    private void handlePayment() {
        final String regNo = txtRegNo.getText();
        final String amount = txtAmount.getText();

        // Immediate UI feedback and button disabling to prevent double submission
        btnPay.setEnabled(false);
        lblStatus.setForeground(Color.BLUE);
        lblStatus.setText("Contacting payment gateway... Please wait.");

        // Asynchronous processing to prevent freezing the Event Dispatch Thread (EDT)
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final FeeService.PaymentReceipt receipt = feeService.processPayment(regNo, amount);
                    lastReceipt = receipt;

                    // Update UI safely on completion
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            lblStatus.setForeground(new Color(0, 128, 0));
                            lblStatus.setText("Payment SUCCESS! Txn: " + receipt.getTransactionId());
                            btnPay.setEnabled(true);
                            showReceiptDialog(receipt);
                        }
                    });
                } catch (final FeeService.ValidationException ex) {
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            lblStatus.setForeground(Color.RED);
                            lblStatus.setText("Validation Error: " + ex.getMessage());
                            btnPay.setEnabled(true);
                        }
                    });
                } catch (final SQLException ex) {
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            lblStatus.setForeground(Color.RED);
                            lblStatus.setText("Database Error: " + ex.getMessage());
                            btnPay.setEnabled(true);
                        }
                    });
                }
            }
        }).start();
    }

    private void handleClear() {
        txtRegNo.setText("");
        txtAmount.setText("");
        lblStatus.setForeground(Color.BLACK);
        lblStatus.setText("Input fields cleared.");
        txtRegNo.requestFocus();
    }

    private void handleReceipt() {
        if (lastReceipt == null) {
            lblStatus.setForeground(Color.RED);
            lblStatus.setText("No successful payment receipt available.");
        } else {
            showReceiptDialog(lastReceipt);
        }
    }

    private void showReceiptDialog(FeeService.PaymentReceipt receipt) {
        final Dialog dialog = new Dialog(this, "Payment Receipt", true);
        dialog.setLayout(new BorderLayout(10, 10));

        TextArea txtArea = new TextArea(receipt.toString(), 6, 40, TextArea.SCROLLBARS_NONE);
        txtArea.setEditable(false);
        txtArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        Button btnClose = new Button("Close");
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        Panel btnPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(btnClose);

        dialog.add(txtArea, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dialog.dispose();
            }
        });
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        FeeService service = new FeeService("", "", "");
        FeeWindow window = new FeeWindow(service);
        window.setVisible(true);
    }
}

