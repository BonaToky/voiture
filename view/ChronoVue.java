package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Locale;

public class ChronoVue extends JPanel {
    private static final long START_MILLIS = -5000L;
    private static final int TICK_MS = 20;

    private long chronoMillis = START_MILLIS;
    private long startNano = 0L;
    private final JLabel timeLabel;
    private final JLabel finishTitleLabel;
    private final JLabel finishValueLabel;
    private final JButton startButton;
    private final JButton stopButton;
    private final JButton resetButton;
    private final Timer timer;
    private boolean finishShown = false;

    public ChronoVue() {
        setPreferredSize(new Dimension(170, 230));
        setBackground(new Color(25, 25, 25));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Chrono");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 16));

        timeLabel = new JLabel(formatTimeMillis(chronoMillis));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeLabel.setForeground(new Color(0, 255, 200));
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 20));

        startButton = createButton("Start", new Color(0, 140, 0));
        stopButton = createButton("Stop", new Color(140, 0, 0));
        resetButton = createButton("Reinitialiser", new Color(0, 0, 140));
        
        finishTitleLabel = new JLabel("Arrivee");
        finishTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        finishTitleLabel.setForeground(new Color(255, 215, 0));
        finishTitleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        finishValueLabel = new JLabel("--");
        finishValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        finishValueLabel.setForeground(new Color(200, 200, 200));
        finishValueLabel.setFont(new Font("Monospaced", Font.BOLD, 14));

        startButton.addActionListener(e -> startChrono());
        stopButton.addActionListener(e -> stopChrono());
        resetButton.addActionListener(e -> resetChrono());

        timer = new Timer(TICK_MS, e -> tick());

        add(title);
        add(Box.createVerticalStrut(10));
        add(timeLabel);
        add(Box.createVerticalStrut(15));
        add(startButton);
        add(Box.createVerticalStrut(8));
        add(stopButton);
        add(Box.createVerticalStrut(8));
        add(resetButton);
        add(Box.createVerticalStrut(10));
        add(finishTitleLabel);
        add(Box.createVerticalStrut(4));
        add(finishValueLabel);
        add(Box.createVerticalGlue());

        updateButtons();
    }

    private void tick() {
        updateChronoNow();
    }

    public void startChrono() {
        if (!timer.isRunning()) {
            chronoMillis = START_MILLIS;
            timeLabel.setText(formatTimeMillis(chronoMillis));
            clearFinish();
            startNano = System.nanoTime();
            timer.start();
            updateButtons();
        }
    }

    private void stopChrono() {
        if (timer.isRunning()) {
            timer.stop();
            updateButtons();
        }
    }

    private void resetChrono() {
        if (timer.isRunning()) {
            timer.stop();
        }
        chronoMillis = START_MILLIS;
        timeLabel.setText(formatTimeMillis(chronoMillis));
        clearFinish();
        updateButtons();
    }

    public void showFinishTime() {
        updateChronoNow();
        showFinishTimeMillis(chronoMillis);
    }

    public void showFinishTimeSeconds(int seconds) {
        showFinishTimeSeconds((double) seconds);
    }

    public void showFinishTimeSeconds(double seconds) {
        if (finishShown) {
            return;
        }
        double safeSeconds = Math.max(0.0, seconds);
        finishValueLabel.setText(formatSeconds(safeSeconds) + " s");
        finishValueLabel.setForeground(new Color(255, 215, 0));
        finishShown = true;
    }

    public void showFinishTimeMillis(long millis) {
        if (finishShown) {
            return;
        }
        long safeMillis = Math.max(0L, millis);
        finishValueLabel.setText(formatTimeMillis(safeMillis));
        finishValueLabel.setForeground(new Color(255, 215, 0));
        finishShown = true;
    }

    public void clearFinish() {
        finishValueLabel.setText("--");
        finishValueLabel.setForeground(new Color(200, 200, 200));
        finishShown = false;
    }

    private void updateButtons() {
        boolean running = timer.isRunning();
        startButton.setEnabled(!running);
        stopButton.setEnabled(running);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(140, 32));

        InputMap inputMap = button.getInputMap(JComponent.WHEN_FOCUSED);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void updateChronoNow() {
        if (!timer.isRunning()) {
            return;
        }
        long elapsedMillis = (System.nanoTime() - startNano) / 1_000_000L;
        chronoMillis = START_MILLIS + elapsedMillis;
        timeLabel.setText(formatTimeMillis(chronoMillis));
    }

    private String formatTimeMillis(long millis) {
        long abs = Math.abs(millis);
        long minutes = abs / 60000;
        long seconds = (abs / 1000) % 60;
        long ms = abs % 1000;
        String sign = millis < 0 ? "-" : "";
        return String.format("%s%02d:%02d:%03d", sign, minutes, seconds, ms);
    }

    private String formatSeconds(double seconds) {
        return String.format(Locale.FRANCE, "%.2f", seconds);
    }
}
