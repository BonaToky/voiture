package modele;

public class Voiture {
    private String nom;
    private double acceleration; // km/h par seconde
    private int vitesseMax;
    
    public Voiture(String nom, double acceleration, int vitesseMax) {
        this.nom = nom;
        this.acceleration = acceleration;
        this.vitesseMax = vitesseMax;
    }
    
    public String getNom() { 
        return nom; 
    }
    
    public double getAcceleration() { 
        return acceleration; 
    }
    
    public int getVitesseMax() { 
        return vitesseMax; 
    }
    
    @Override
    public String toString() {
        return nom + " (0-" + vitesseMax + " km/h en " + String.format("%.1f", vitesseMax/acceleration) + "s)";
    }
}
