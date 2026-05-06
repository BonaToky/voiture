package view;

import modele.CompteurVitesseModel;
import modele.Voiture;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class CompteurVitesseVue extends JPanel {
    private CompteurVitesseModel model;

    public CompteurVitesseVue(CompteurVitesseModel model) {
        this.model = model;
        setPreferredSize(new Dimension(500, 500));
        setBackground(new Color(30, 30, 30));
        setFocusable(true);
        
        model.addChangeListener(e -> repaint());
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (model.getVoitureActuelle() == null) {
            drawNoCarMessage(g2);
            return;
        }
        
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height) - 80;
        int x = (width - size) / 2;
        int y = (height - size) / 2;
        
        drawCadran(g2, x, y, size);
        drawGraduations(g2, width, height, size);
        drawDigitalDisplay(g2, width, height);
        drawNeedle(g2, width, height, size);
        drawCarInfo(g2);
        drawInstructions(g2);
    }
    
    private void drawNoCarMessage(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        String msg = "Aucune voiture sélectionnée";
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(msg)) / 2;
        int y = getHeight() / 2;
        g2.drawString(msg, x, y);
        
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        String msg2 = "Chargez un fichier CSV via le menu Fichier";
        fm = g2.getFontMetrics();
        x = (getWidth() - fm.stringWidth(msg2)) / 2;
        g2.drawString(msg2, x, y + 30);
    }
    
    private void drawCadran(Graphics2D g2, int x, int y, int size) {
        g2.setColor(new Color(45, 45, 45));
        g2.fillOval(x, y, size, size);
        g2.setStroke(new BasicStroke(5));
        g2.setColor(Color.GRAY);
        g2.drawOval(x, y, size, size);
        
        // if (model.getVoitureActuelle() != null) {
        //     int vitesseMax = model.getVoitureActuelle().getVitesseMax();
        //     int dangerStart = (int)(vitesseMax * 0.8);
        //     g2.setColor(new Color(255, 0, 0, 50));
        //     double startAngle = 135 + (dangerStart * 270.0 / vitesseMax);
        //     double endAngle = 135 + (vitesseMax * 270.0 / vitesseMax);
        //     g2.fillArc(x, y, size, size, (int)startAngle, (int)(endAngle - startAngle));
        // }
    }
    
    private void drawGraduations(Graphics2D g2, int width, int height, int size) {
        if (model.getVoitureActuelle() == null) return;
        
        int vitesseMax = model.getVoitureActuelle().getVitesseMax();
        g2.setColor(Color.WHITE);
        
        int interval = vitesseMax <= 100 ? 10 : (vitesseMax <= 200 ? 20 : 30);
        
        for (int i = 0; i <= vitesseMax; i += interval) {
            double angle = Math.toRadians(135 + (i * 270.0 / vitesseMax));
            
            int rOut = size / 2 - 10;
            int rIn = size / 2 - 30;
            
            int x1 = (int) (width / 2 + Math.cos(angle) * rIn);
            int y1 = (int) (height / 2 + Math.sin(angle) * rIn);
            int x2 = (int) (width / 2 + Math.cos(angle) * rOut);
            int y2 = (int) (height / 2 + Math.sin(angle) * rOut);
            
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(x1, y1, x2, y2);
            
            int rText = size / 2 - 50;
            int tx = (int) (width / 2 + Math.cos(angle) * rText) - 15;
            int ty = (int) (height / 2 + Math.sin(angle) * rText) + 5;
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(String.valueOf(i), tx, ty);
        }
    }
    
    private void drawDigitalDisplay(Graphics2D g2, int width, int height) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(width / 2 - 70, height / 2 + 40, 140, 70, 10, 10);
        
        if (model.isAccelerating()) {
            g2.setColor(Color.GREEN);
        } else {
            g2.setColor(Color.CYAN);
        }
        g2.setFont(new Font("Monospaced", Font.BOLD, 45));
        String vStr = String.format("%03d", (int) model.getVitesseActuelle());
        g2.drawString(vStr, width / 2 - 40, height / 2 + 70);
        
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.drawString("km/h", width / 2 - 20, height / 2 + 95);
        
        if (model.isAccelerating()) {
            g2.setColor(Color.GREEN);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("ACCELERATION", width / 2 - 50, height / 2 + 120);
        }
    }
    
    private void drawNeedle(Graphics2D g2, int width, int height, int size) {
        if (model.getVoitureActuelle() == null) return;
        
        int vitesseMax = model.getVoitureActuelle().getVitesseMax();
        double angleAiguille = Math.toRadians(135 + (model.getVitesseActuelle() * 270.0 / vitesseMax));
        int rAiguille = size / 2 - 45;
        int ax = (int) (width / 2 + Math.cos(angleAiguille) * rAiguille);
        int ay = (int) (height / 2 + Math.sin(angleAiguille) * rAiguille);
        
        g2.setColor(model.isAccelerating() ? new Color(255, 100, 100) : Color.RED);
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(width / 2, height / 2, ax, ay);
        
        g2.setColor(new Color(255, 0, 0, 50));
        g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(width / 2, height / 2, ax, ay);
        
        g2.setColor(Color.YELLOW);
        g2.fillOval(width / 2 - 10, height / 2 - 10, 20, 20);
        g2.setColor(Color.BLACK);
        g2.fillOval(width / 2 - 5, height / 2 - 5, 10, 10);
    }
    
    private void drawCarInfo(Graphics2D g2) {
        if (model.getVoitureActuelle() == null) return;
        
        Voiture voiture = model.getVoitureActuelle();
        
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(10, 10, 260, 135, 10, 10);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("Voiture: " + voiture.getNom(), 20, 35);
        
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString("Vitesse Max: " + voiture.getVitesseMax() + " km/h", 20, 55);
        g2.drawString("Accélération: " + String.format("%.1f", voiture.getAcceleration()) + " km/h/s", 20, 75);

        String consoText = String.format("Conso Nitro: %.1f kg/min", voiture.getConsoNitroKgMin());
        g2.drawString(consoText, 20, 95);

        String nitroState = model.isNitroActive() ? "ON" : "OFF";
        String nitroText = String.format("Nitro: %.1f/%.1f kg (%s)",
                model.getNitroRestantKg(), model.getNitroCapaciteKg(), nitroState);
        g2.drawString(nitroText, 20, 115);

        double capKg = model.getNitroCapaciteKg();
        double remainingKg = model.getNitroRestantKg();
        double ratio = capKg > 0.0 ? remainingKg / capKg : 0.0;
        ratio = Math.max(0.0, Math.min(1.0, ratio));

        int barX = 20;
        int barY = 125;
        int barW = 220;
        int barH = 10;

        g2.setColor(new Color(20, 20, 20));
        g2.fillRoundRect(barX, barY, barW, barH, 6, 6);
        g2.setColor(new Color(70, 70, 70));
        g2.drawRoundRect(barX, barY, barW, barH, 6, 6);

        int fillW = (int) Math.round(barW * ratio);
        if (fillW > 0) {
            Color fillColor;
            if (ratio > 0.5) {
                fillColor = new Color(0, 180, 90);
            } else if (ratio > 0.2) {
                fillColor = new Color(200, 160, 0);
            } else {
                fillColor = new Color(200, 60, 60);
            }
            g2.setColor(fillColor);
            g2.fillRoundRect(barX, barY, fillW, barH, 6, 6);
        }
    }
    
    private void drawInstructions(Graphics2D g2) {
        g2.setColor(new Color(255, 255, 255, 100));
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        
        String instructions = "ESPACE: Accélérer | N: Nitro | R: Réinitialiser | D: Décélérer";
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(instructions)) / 2;
        int y = getHeight() - 20;
        
        g2.drawString(instructions, x, y);
    }
}