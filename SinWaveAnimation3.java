package sinwaveanimation3;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.Polygon;

public class SinWaveAnimation3 extends JPanel {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static final int POLYGON_RADIUS = 100; // Renamed for generality
    private static final int CENTER_X_POLYGON = WIDTH / 4; // Renamed for generality
    private static final int CENTER_Y_POLYGON = HEIGHT / 2; // Renamed for generality
    private static final int GRAPH_OFFSET_X = WIDTH / 2 - 50;
    private static final int GRAPH_OFFSET_Y = HEIGHT / 2;

    private int numSides = 6; // Default to hexagon, now an instance variable
    private double angle = 0;
    private Timer timer;

    public SinWaveAnimation3() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK); // Default background

        timer = new Timer(20, e -> { // Initial speed 20ms delay
            angle += 0.05; // Increment angle for animation
            if (angle > 2 * Math.PI) {
                angle = 0; // Reset angle after a full cycle
            }
            repaint();
        });
        timer.start(); // Animation starts automatically
    }

    // --- Control Methods ---
    public void setNumSides(int sides) {
        this.numSides = sides;
        repaint(); // Redraw the polygon with new sides
    }

    public void setAnimationSpeed(int delay) {
        timer.setDelay(delay); // Set the timer's delay in milliseconds
    }

    public void toggleAnimation() {
        if (timer.isRunning()) {
            timer.stop();
        } else {
            timer.start();
        }
    }

    // --- Helper Methods for Polygon Tracing ---
    private double getPolygonRelativeX(double currentAngle, int radius, int numSides) {
        double segmentAngleWidth = 2 * Math.PI / numSides;
        int segmentIndex = (int) (currentAngle / segmentAngleWidth);
        if (segmentIndex >= numSides) {
            segmentIndex = 0;
        }

        double startVertexAngle = segmentIndex * segmentAngleWidth;
        double endVertexAngle = (segmentIndex + 1) * segmentAngleWidth;
        if (segmentIndex == numSides - 1) {
            endVertexAngle = 2 * Math.PI;
        }

        double x1_relative = radius * Math.cos(startVertexAngle);
        double x2_relative = radius * Math.cos(endVertexAngle);

        double t = (currentAngle - startVertexAngle) / segmentAngleWidth;
        return x1_relative * (1 - t) + x2_relative * t;
    }

    private double getPolygonRelativeY(double currentAngle, int radius, int numSides) {
        double segmentAngleWidth = 2 * Math.PI / numSides;
        int segmentIndex = (int) (currentAngle / segmentAngleWidth);
        if (segmentIndex >= numSides) {
            segmentIndex = 0;
        }

        double startVertexAngle = segmentIndex * segmentAngleWidth;
        double endVertexAngle = (segmentIndex + 1) * segmentAngleWidth;
        if (segmentIndex == numSides - 1) {
            endVertexAngle = 2 * Math.PI;
        }

        double y1_relative = radius * Math.sin(startVertexAngle);
        double y2_relative = radius * Math.sin(endVertexAngle);

        double t = (currentAngle - startVertexAngle) / segmentAngleWidth;
        return y1_relative * (1 - t) + y2_relative * t;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- Draw Polygon (Hexagon/Octagon/etc.) ---
        g2d.setColor(Color.WHITE);
        int[] xPolygon = new int[numSides];
        int[] yPolygon = new int[numSides];

        for (int i = 0; i < numSides; i++) {
            double currentVertexAngle = 2 * Math.PI * i / numSides;
            xPolygon[i] = (int) (CENTER_X_POLYGON + POLYGON_RADIUS * Math.cos(currentVertexAngle));
            yPolygon[i] = (int) (CENTER_Y_POLYGON + POLYGON_RADIUS * Math.sin(currentVertexAngle));
        }
        g2d.draw(new Polygon(xPolygon, yPolygon, numSides));


        // Draw axes for the polygon
        g2d.setColor(Color.WHITE);
        g2d.drawLine(CENTER_X_POLYGON - POLYGON_RADIUS - 20, CENTER_Y_POLYGON,
                CENTER_X_POLYGON + POLYGON_RADIUS + 20, CENTER_Y_POLYGON); // X-axis
        g2d.drawLine(CENTER_X_POLYGON, CENTER_Y_POLYGON - POLYGON_RADIUS - 20,
                CENTER_X_POLYGON, CENTER_Y_POLYGON + POLYGON_RADIUS + 20); // Y-axis
        g2d.drawString("0", CENTER_X_POLYGON + POLYGON_RADIUS + 5, CENTER_Y_POLYGON + 5);
        g2d.drawString("\u03c0/2", CENTER_X_POLYGON - 10, CENTER_Y_POLYGON - POLYGON_RADIUS - 5);
        g2d.drawString("\u03c0", CENTER_X_POLYGON - POLYGON_RADIUS - 20, CENTER_Y_POLYGON + 5);
        g2d.drawString("3\u03c0/2", CENTER_X_POLYGON - 10, CENTER_Y_POLYGON + POLYGON_RADIUS + 15);


        // --- Draw Moving Point on Polygon ---
        double pointX = CENTER_X_POLYGON + getPolygonRelativeX(angle, POLYGON_RADIUS, numSides);
        double pointY = CENTER_Y_POLYGON - getPolygonRelativeY(angle, POLYGON_RADIUS, numSides);
        g2d.setColor(Color.RED);
        g2d.fill(new Ellipse2D.Double(pointX - 5, pointY - 5, 10, 10)); // Draw the moving point

        // Draw line from center to the moving point
        g2d.setColor(Color.DARK_GRAY);
        g2d.draw(new Line2D.Double(CENTER_X_POLYGON, CENTER_Y_POLYGON, pointX, pointY));

        // Draw projection line from the point to the x-axis of the polygon
        g2d.setColor(Color.ORANGE);
        g2d.draw(new Line2D.Double(pointX, pointY, pointX, CENTER_Y_POLYGON));


        // --- Draw Polygon Wave Graph ---
        g2d.setColor(Color.WHITE);
        g2d.drawLine(GRAPH_OFFSET_X, GRAPH_OFFSET_Y, GRAPH_OFFSET_X + (int)(2 * Math.PI * POLYGON_RADIUS) + 50, GRAPH_OFFSET_Y); // X-axis
        g2d.drawLine(GRAPH_OFFSET_X, GRAPH_OFFSET_Y - POLYGON_RADIUS - 20, GRAPH_OFFSET_X, GRAPH_OFFSET_Y + POLYGON_RADIUS + 20); // Y-axis

        // Mark key points on polygon graph x-axis
        g2d.drawString("0", GRAPH_OFFSET_X - 15, GRAPH_OFFSET_Y + 5);
        g2d.drawString("\u03c0/2", GRAPH_OFFSET_X + (int)(Math.PI/2 * POLYGON_RADIUS) - 10, GRAPH_OFFSET_Y + 15);
        g2d.drawString("\u03c0", GRAPH_OFFSET_X + (int)(Math.PI * POLYGON_RADIUS) - 5, GRAPH_OFFSET_Y + 15);
        g2d.drawString("3\u03c0/2", GRAPH_OFFSET_X + (int)(3*Math.PI/2 * POLYGON_RADIUS) - 10, GRAPH_OFFSET_Y + 15);
        g2d.drawString("2\u03c0", GRAPH_OFFSET_X + (int)(2*Math.PI * POLYGON_RADIUS) - 5, GRAPH_OFFSET_Y + 15);


        // Plot polygon wave
        g2d.setColor(Color.GREEN.darker());
        for (double i = 0; i <= angle; i += 0.01) {
            double x = GRAPH_OFFSET_X + i * POLYGON_RADIUS;
            double y = GRAPH_OFFSET_Y - getPolygonRelativeY(i, POLYGON_RADIUS, numSides);
            g2d.fill(new Ellipse2D.Double(x - 1, y - 1, 2, 2)); // Draw small points to form the wave
        }

        // Draw horizontal line from polygon point to polygon wave
        g2d.setColor(Color.MAGENTA);
        double currentPolygonWaveX = GRAPH_OFFSET_X + angle * POLYGON_RADIUS;
        double currentPolygonWaveY = GRAPH_OFFSET_Y - getPolygonRelativeY(angle, POLYGON_RADIUS, numSides);
        g2d.draw(new Line2D.Double(pointX, pointY, currentPolygonWaveX, currentPolygonWaveY));

        // Draw vertical line from current polygon wave point to x-axis of wave
        g2d.draw(new Line2D.Double(currentPolygonWaveX, currentPolygonWaveY, currentPolygonWaveX, GRAPH_OFFSET_Y));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Polygon Wave Animation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout()); // Use BorderLayout for frame

        SinWaveAnimation3 animationPanel = new SinWaveAnimation3();
        frame.add(animationPanel, BorderLayout.CENTER); // Animation in the center

        // --- Control Panel ---
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout()); // Use FlowLayout for controls

        // 1. Start/Stop Button
        JButton startStopButton = new JButton("Stop");
        startStopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationPanel.toggleAnimation();
                if (animationPanel.timer.isRunning()) {
                    startStopButton.setText("Stop");
                } else {
                    startStopButton.setText("Start");
                }
            }
        });
        controlPanel.add(startStopButton);

        // 2. Number of Sides Chooser
        Integer[] sidesOptions = new Integer[18]; // For 3 to 20 sides
        for (int i = 0; i < sidesOptions.length; i++) {
            sidesOptions[i] = i + 3; // Start from 3 (triangle)
        }
        JComboBox<Integer> sidesComboBox = new JComboBox<>(sidesOptions);
        sidesComboBox.setSelectedItem(animationPanel.numSides); // Set default
        sidesComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationPanel.setNumSides((Integer) sidesComboBox.getSelectedItem());
            }
        });
        controlPanel.add(new JLabel("Sides:"));
        controlPanel.add(sidesComboBox);

        // 3. Animation Speed Slider
        // Delay: 1ms (fastest) to 200ms (slowest)
        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 200, 20); // Initial delay 20ms
        speedSlider.setMajorTickSpacing(50);
        speedSlider.setMinorTickSpacing(10);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                animationPanel.setAnimationSpeed(speedSlider.getValue());
            }
        });
        controlPanel.add(new JLabel("Speed (delay ms):"));
        controlPanel.add(speedSlider);

        // 4. Background Color Selector
        JButton bgColorButton = new JButton("Background Color");
        bgColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(frame, "Choose Background Color", animationPanel.getBackground());
                if (newColor != null) {
                    animationPanel.setBackground(newColor);
                }
            }
        });
        controlPanel.add(bgColorButton);


        frame.add(controlPanel, BorderLayout.SOUTH); // Controls at the bottom

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
