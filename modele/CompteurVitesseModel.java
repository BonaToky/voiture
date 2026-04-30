package modele;

import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class CompteurVitesseModel {
    private static final double DISTANCE_SCALE = 1.0;
    private double vitesseActuelle = 0; // km/h
    private Voiture voitureActuelle = null;
    private Piste pisteActuelle = null;
    private EventListenerList listeners = new EventListenerList();
    private Timer animationTimer;
    private boolean accelerating = false;
    private long dernierTemps;
    private double tempsCourse = 0; // secondes
    private boolean courseEnCours = false;
    
    public CompteurVitesseModel() {
        dernierTemps = System.currentTimeMillis();
        startAnimationTimer();
    }
    
    public double getVitesseActuelle() { return vitesseActuelle; }
    public Voiture getVoitureActuelle() { return voitureActuelle; }
    public Piste getPisteActuelle() { return pisteActuelle; }
    public int getTempsCourseSecondes() { return (int) Math.round(tempsCourse); }
    
    public void setVoiture(Voiture voiture) {
        this.voitureActuelle = voiture;
        this.vitesseActuelle = 0;
        this.accelerating = false;
        this.tempsCourse = 0;
        this.courseEnCours = false;
        stopAcceleration();
        if (pisteActuelle != null) {
            pisteActuelle.reset();
        }
        fireStateChanged();
    }
    
    public void setPiste(Piste piste) {
        this.pisteActuelle = piste;
        this.tempsCourse = 0;
        this.courseEnCours = false;
        if (pisteActuelle != null) {
            pisteActuelle.reset();
        }
        fireStateChanged();
    }
    
    public void setVitesse(double vitesse) {
        if (voitureActuelle != null) {
            this.vitesseActuelle = Math.min(vitesse, voitureActuelle.getVitesseMax());
            this.vitesseActuelle = Math.max(0, this.vitesseActuelle);
            fireStateChanged();
        }
    }
    
    public void startAcceleration() {
        if (voitureActuelle != null && pisteActuelle != null && !pisteActuelle.estTerminee()) {
            if (!accelerating) {
                accelerating = true;
                dernierTemps = System.currentTimeMillis();
            }
        }
    }
    
    public void stopAcceleration() {
        accelerating = false;
    }
    
    private void startAnimationTimer() {
        animationTimer = new Timer(16, e -> { // ~60 FPS pour l'animation de la voiture
            if (voitureActuelle == null || pisteActuelle == null) {
                dernierTemps = System.currentTimeMillis();
                return;
            }

            long tempsActuel = System.currentTimeMillis();
            double deltaTime = (tempsActuel - dernierTemps) / 1000.0; // en secondes
            dernierTemps = tempsActuel;

            // Limiter deltaTime pour eviter les bonds trop grands
            if (deltaTime > 0.1) deltaTime = 0.1;

            boolean doitAvancer = (accelerating || vitesseActuelle > 0) && !pisteActuelle.estTerminee();
            if (doitAvancer) {
                updateMotion(deltaTime);

                // Verifier si la piste est terminee
                if (pisteActuelle.estTerminee()) {
                    vitesseActuelle = 0;
                    accelerating = false;
                    courseEnCours = false;
                }

                fireStateChanged();
            }
        });
        animationTimer.start();
    }
    
    public void decelerer() {
        if (voitureActuelle != null && pisteActuelle != null) {
            double decel = voitureActuelle.getAcceleration() * 0.1;
            vitesseActuelle = Math.max(0, vitesseActuelle - decel);
            fireStateChanged();
        }
    }
    
    public void resetVitesse() {
        setVitesse(0);
        accelerating = false;
        tempsCourse = 0;
        courseEnCours = false;
        stopAcceleration();
        if (pisteActuelle != null) {
            pisteActuelle.reset();
        }
        fireStateChanged();
    }

    public void stopCourse() {
        stopAcceleration();
        vitesseActuelle = 0;
        courseEnCours = false;
        fireStateChanged();
    }

    private void updateMotion(double deltaTime) {
        if (voitureActuelle == null || pisteActuelle == null || pisteActuelle.estTerminee()) {
            return;
        }

        if (accelerating) {
            double acceleration = voitureActuelle.getAcceleration();
            if (acceleration > 0) {
                double vitesseMax = voitureActuelle.getVitesseMax();
                vitesseActuelle = Math.min(vitesseActuelle + acceleration * deltaTime, vitesseMax);
            } else {
                vitesseActuelle = 0;
                accelerating = false;
            }
        }

        if (!courseEnCours && (accelerating || vitesseActuelle > 0)) {
            if (pisteActuelle.getDistanceParcourue() <= 0) {
                tempsCourse = 0;
            }
            courseEnCours = true;
        }

        if (courseEnCours) {
            tempsCourse += deltaTime;
        }

        if (vitesseActuelle > 0) {
            double distanceKm = (vitesseActuelle * deltaTime) / 3600.0;
            double distanceScaled = distanceKm * DISTANCE_SCALE;
            pisteActuelle.ajouterDistance(distanceScaled);
        }
    }
    
    public boolean isAccelerating() {
        return accelerating;
    }
    
    public void addChangeListener(ChangeListener listener) {
        listeners.add(ChangeListener.class, listener);
    }
    
    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
            listener.stateChanged(event);
        }
    }
}