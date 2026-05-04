package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class ChronoVue extends JPanel {
    private static final int START_SECONDS = -5;
    private static final int TICK_MS = 1000;

    private int chronoSeconds = START_SECONDS;
    private int elapsedSeconds = 0;
    private final JLabel timeLabel;
    private final JLabel finishLabel;
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

        timeLabel = new JLabel(formatTime(chronoSeconds));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeLabel.setForeground(new Color(0, 255, 200));
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 28));

        startButton = createButton("Start", new Color(0, 140, 0));
        stopButton = createButton("Stop", new Color(140, 0, 0));
        resetButton = createButton("Reinitialiser", new Color(0, 0, 140));

        finishLabel = new JLabel("Arrivee: --");
        finishLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        finishLabel.setForeground(new Color(200, 200, 200));
        finishLabel.setFont(new Font("Arial", Font.PLAIN, 12));

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
        add(Box.createVerticalStrut(12));
        add(finishLabel);
        add(Box.createVerticalGlue());

        updateButtons();
    }

    private void tick() {
        chronoSeconds++;
        elapsedSeconds++;
        timeLabel.setText(formatTime(chronoSeconds));
    }

    public void startChrono() {
        if (!timer.isRunning()) {
            chronoSeconds = START_SECONDS;
            elapsedSeconds = 0;
            timeLabel.setText(formatTime(chronoSeconds));
            clearFinish();
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
        chronoSeconds = START_SECONDS;
        elapsedSeconds = 0;
        timeLabel.setText(formatTime(chronoSeconds));
        clearFinish();
        updateButtons();
    }

    public void showFinishTime() {
        showFinishTimeSeconds(elapsedSeconds);
    }

    public void showFinishTimeSeconds(int seconds) {
        if (finishShown) {
            return;
        }
        int safeSeconds = Math.max(0, seconds);
        stopChrono();
        finishLabel.setText("Arrivee: " + safeSeconds + " s");
        finishLabel.setForeground(new Color(255, 215, 0));
        finishShown = true;
    }

    public void clearFinish() {
        finishLabel.setText("Arrivee: --");
        finishLabel.setForeground(new Color(200, 200, 200));
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

    private String formatTime(int seconds) {
        int abs = Math.abs(seconds);
        int minutes = abs / 60;
        int secs = abs % 60;
        String sign = seconds < 0 ? "-" : "";
        return String.format("%s%02d:%02d", sign, minutes, secs);
    }
}
