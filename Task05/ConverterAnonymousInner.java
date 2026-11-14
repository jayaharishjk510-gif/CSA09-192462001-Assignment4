package task05;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Style C: Anonymous Inner Class.
 * Instantiates an inline anonymous implementation of ActionListener.
 */
public class ConverterAnonymousInner extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JButton convertButton = new JButton("Convert");
    private final JLabel result = new JLabel(" ");

    private static double rate(String currency) {
        return currency.equals("USD") ? 0.012 : 0.011;
    }

    public ConverterAnonymousInner() {
        super("Converter - Style C (Anonymous Inner Class)");
        setLayout(new FlowLayout());

        add(new JLabel("INR"));
        add(amount);
        add(target);
        add(convertButton);
        add(result);

        // Anonymous Inner Class assigned to a shared reference
        ActionListener anonymousListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = amount.getText().trim();
                    if (text.isEmpty()) throw new NumberFormatException();
                    double val = Double.parseDouble(text);
                    if (val < 0) throw new NumberFormatException();

                    String curr = (String) target.getSelectedItem();
                    double res = val * rate(curr);
                    String message = String.format("Converted Value: %.2f %s", res, curr);

                    result.setText(message);
                    // Requirement 5.2: Display in dialog box
                    JOptionPane.showMessageDialog(ConverterAnonymousInner.this, message, "Conversion Result", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    result.setText("Invalid amount");
                    JOptionPane.showMessageDialog(ConverterAnonymousInner.this, "Please enter a valid positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        convertButton.addActionListener(anonymousListener);
        amount.addActionListener(anonymousListener);

        // Window closing via WindowAdapter
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConverterAnonymousInner().setVisible(true));
    }
}
