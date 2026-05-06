package modele;

public class Voiture {
    private String nom;
    private double acceleration; // km/h par seconde
    private int vitesseMax;
    private double consoNitroKgMin;
    private double variationAcceleration;
    private double capaciteNitroKg;
    
    public Voiture(String nom, double acceleration, int vitesseMax) {
        this(nom, acceleration, vitesseMax, 0.0, 0.0, 0.0);
    }

    public Voiture(String nom, double acceleration, int vitesseMax, double consoNitroKgMin,
                   double variationAcceleration, double capaciteNitroKg) {
        this.nom = nom;
        this.acceleration = acceleration;
        this.vitesseMax = vitesseMax;
        this.consoNitroKgMin = consoNitroKgMin;
        this.variationAcceleration = variationAcceleration;
        this.capaciteNitroKg = capaciteNitroKg;
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

    public double getConsoNitroKgMin() {
        return consoNitroKgMin;
    }

    public double getVariationAcceleration() {
        return variationAcceleration;
    }

    public double getCapaciteNitroKg() {
        return capaciteNitroKg;
    }
    
    @Override
    public String toString() {
        return nom + " (0-" + vitesseMax + " km/h en " + String.format("%.1f", vitesseMax/acceleration) + "s)";
    }
}
