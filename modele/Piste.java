package modele;

public class Piste {
    private String nom;
    private double distanceKm; // Distance totale en km
    private double distanceParcourue; // Distance parcourue en km
    
    public Piste(String nom, double distanceKm) {
        this.nom = nom;
        this.distanceKm = distanceKm;
        this.distanceParcourue = 0;
    }
    
    public String getNom() { return nom; }
    public double getDistanceKm() { return distanceKm; }
    public double getDistanceParcourue() { return distanceParcourue; }
    
    public void setDistanceParcourue(double distance) {
        this.distanceParcourue = Math.min(distance, distanceKm);
        this.distanceParcourue = Math.max(0, this.distanceParcourue);
    }
    
    public void ajouterDistance(double distance) {
        double nouvelleDistance = distanceParcourue + distance;
        if (nouvelleDistance >= distanceKm) {
            this.distanceParcourue = distanceKm;
        } else {
            this.distanceParcourue = nouvelleDistance;
        }
    }
    
    public boolean estTerminee() {
        return distanceParcourue >= distanceKm;
    }
    
    public void reset() {
        distanceParcourue = 0;
    }
    
    public double getPourcentage() {
        return (distanceParcourue / distanceKm) * 100;
    }

    private String formatDistanceKm() {
        if (distanceKm == Math.rint(distanceKm)) {
            return String.format(java.util.Locale.US, "%.0f", distanceKm);
        }
        return String.format(java.util.Locale.US, "%.1f", distanceKm);
    }
    
    @Override
    public String toString() {
        return nom + " (" + formatDistanceKm() + " km)";
    }
}