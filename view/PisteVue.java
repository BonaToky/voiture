package view;

import modele.Piste;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class PisteVue extends JPanel {
    private Piste piste;
    private static final int PISTE_WIDTH = 30; // 30 cm en représentation
    
    public PisteVue(Piste piste) {
        this.piste = piste;
        setPreferredSize(new Dimension(400, 200));
        setBackground(new Color(20, 20, 20));
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
    }
    
    public void setPiste(Piste piste) {
        this.piste = piste;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (piste == null) {
            drawNoPisteMessage(g2);
            return;
        }
        
        int width = getWidth();
        int height = getHeight();
        int pisteY = height / 2 - 20;
        int pisteHeight = 40;
        
        // Dessiner le fond de la piste
        g2.setColor(new Color(40, 40, 40));
        g2.fillRoundRect(20, pisteY, width - 40, pisteHeight, 10, 10);
        
        // Dessiner la bande de roulement
        g2.setColor(new Color(60, 60, 60));
        g2.fillRoundRect(22, pisteY + 5, width - 44, pisteHeight - 10, 8, 8);
        
        // Dessiner les lignes de la piste
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        for (int i = 0; i <= 4; i++) {
            int x = 20 + (i * (width - 40) / 4);
            g2.drawLine(x, pisteY, x, pisteY + pisteHeight);
        }
        
        // Ligne d'arrivée (fin de la piste)
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(width - 20, pisteY, width - 20, pisteY + pisteHeight);
        
        // Zone de départ (début de la piste)
        g2.setColor(Color.GREEN);
        g2.drawLine(20, pisteY, 20, pisteY + pisteHeight);
        
        // Calculer la position de la voiture
        double pourcentage = piste.getPourcentage();
        int positionX = 20 + (int)((width - 40) * (pourcentage / 100.0));
        positionX = Math.min(positionX, width - 30);
        positionX = Math.max(positionX, 20);
        
        int carWidth = 34;
        int carHeight = 18;
        int carX = positionX - carWidth / 2;
        int carY = pisteY + (pisteHeight - carHeight) / 2;

        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(carX + 2, carY + 3, carWidth, carHeight, 8, 8);

        GradientPaint bodyGradient = new GradientPaint(
            carX, carY, new Color(220, 40, 40),
            carX + carWidth, carY + carHeight, new Color(255, 140, 0));
        g2.setPaint(bodyGradient);
        g2.fillRoundRect(carX, carY, carWidth, carHeight, 8, 8);

        g2.setColor(new Color(255, 255, 255, 120));
        g2.fillRoundRect(carX + 6, carY + 6, carWidth - 12, 3, 3, 3);

        g2.setColor(new Color(120, 10, 10));
        g2.fillRoundRect(carX + 2, carY + carHeight - 4, carWidth - 4, 4, 4, 4);

        int roofWidth = 18;
        int roofHeight = 7;
        int roofX = carX + (carWidth - roofWidth) / 2;
        int roofY = carY - 2;

        g2.setColor(new Color(180, 50, 50));
        g2.fillRoundRect(roofX, roofY, roofWidth, roofHeight, 5, 5);

        g2.setColor(new Color(120, 200, 220));
        g2.fillRoundRect(roofX + 2, roofY + 2, roofWidth - 4, roofHeight - 3, 3, 3);

        g2.setColor(new Color(255, 240, 180));
        g2.fillOval(carX + carWidth - 4, carY + 5, 4, 4);
        g2.setColor(new Color(200, 30, 30));
        g2.fillRect(carX, carY + 6, 3, 4);

        int wheelSize = 6;
        int wheelY = carY + carHeight - 2;
        int wheelRearX = carX + 4;
        int wheelFrontX = carX + carWidth - 10;
        g2.setColor(new Color(10, 10, 10));
        g2.fillOval(wheelRearX, wheelY, wheelSize, wheelSize);
        g2.fillOval(wheelFrontX, wheelY, wheelSize, wheelSize);
        g2.setColor(new Color(160, 160, 160));
        g2.fillOval(wheelRearX + 2, wheelY + 2, 2, 2);
        g2.fillOval(wheelFrontX + 2, wheelY + 2, 2, 2);
        
        // Effet de vitesse (traînée)
        if (piste.getPourcentage() > 0 && piste.getPourcentage() < 100) {
            g2.setColor(new Color(255, 255, 255, 100));
            for (int i = 1; i <= 3; i++) {
                int trailX = positionX - (i * 8);
                if (trailX > 20) {
                    g2.fillOval(trailX, carY + carHeight / 2 - 2, 3, 3);
                }
            }
        }
        
        // Informations de la piste
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        String info = String.format("%s - %.1f / %.1f km (%.1f%%)", 
            piste.getNom(), piste.getDistanceParcourue(), piste.getDistanceKm(), piste.getPourcentage());
        FontMetrics fm = g2.getFontMetrics();
        int x = (width - fm.stringWidth(info)) / 2;
        g2.drawString(info, x, pisteY - 15);
        
        // Barre de progression
        int progressWidth = (int)((width - 40) * (piste.getPourcentage() / 100.0));
        g2.setColor(new Color(0, 255, 0, 100));
        g2.fillRoundRect(20, pisteY + pisteHeight + 5, progressWidth, 10, 5, 5);
        
        // Animation de scintillement pour la ligne d'arrivée
        if (piste.estTerminee()) {
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            String finish = "ARRIVÉE !";
            fm = g2.getFontMetrics();
            x = (width - fm.stringWidth(finish)) / 2;
            g2.drawString(finish, x, pisteY - 30);
        }
    }
    
    private void drawNoPisteMessage(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        String msg = "Aucune piste chargée";
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(msg)) / 2;
        int y = getHeight() / 2;
        g2.drawString(msg, x, y);
    }
}
