package task04;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Repaired DrawPad program meeting all SIMATS CO4 Task 4 requirements.
 * Solves the silent method override typo defect, updates the status label,
 * stores clicked coordinates in memory, and redraws them inside paint()
 * so points persist across minimize/restore and window obscuring.
 */
public class DrawPad extends Frame {
    private final List<Point> points = new ArrayList<>();
    private final Label status = new Label("Clicks: 0");

    public DrawPad() {
        super("Draw Pad");
        add(status, BorderLayout.SOUTH);

        addMouseListener(new MouseAdapter() {
            // TODO 1: Override mouseClicked correctly: store the point, update the
            // label and request a repaint
            @Override
            public void mouseClicked(MouseEvent e) {
                // Store click coordinates
                points.add(new Point(e.getX(), e.getY()));
                // Update status label with current count
                status.setText("Clicks: " + points.size());
                // Request AWT to schedule a repaint via the Event Dispatch Thread
                repaint();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setSize(400, 300);
        setLocationRelativeTo(null);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        // TODO 2: Draw every stored point as a 6 × 6 filled circle centred on the point
        g.setColor(new Color(20, 60, 160));
        for (Point p : points) {
            // Centering a 6x6 circle requires offsetting the bounding box by radius (3px)
            g.fillOval(p.x - 3, p.y - 3, 6, 6);
        }
    }

    public static void main(String[] args) {
        new DrawPad().setVisible(true);
    }
}
