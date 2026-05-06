package util;

import modele.CompteurVitesseModel;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

public class GlobalKeyHandler implements KeyEventDispatcher {
    private CompteurVitesseModel model;
    private boolean spacePressed = false;
    private boolean nitroPressed = false;
    
    public GlobalKeyHandler(CompteurVitesseModel model) {
        this.model = model;
        // Enregistrer le handler global
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        // Vérifier si on a une voiture et une piste
        if (model.getVoitureActuelle() == null || model.getPisteActuelle() == null) {
            return false;
        }
        
        // Touche ESPACE enfoncée
        if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!spacePressed) {
                spacePressed = true;
                if (!model.getPisteActuelle().estTerminee()) {
                    model.startAcceleration();
                }
            }
            // Consommer l'événement pour qu'il n'affecte pas les autres composants
            e.consume();
            return true;
        }
        
        // Touche ESPACE relâchée
        if (e.getID() == KeyEvent.KEY_RELEASED && e.getKeyCode() == KeyEvent.VK_SPACE) {
            spacePressed = false;
            model.stopAcceleration();
            e.consume();
            return true;
        }

        // Touche N pour activer le nitro
        if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_N) {
            if (!nitroPressed) {
                nitroPressed = true;
                model.startNitro();
            }
            e.consume();
            return true;
        }

        // Touche N relachee
        if (e.getID() == KeyEvent.KEY_RELEASED && e.getKeyCode() == KeyEvent.VK_N) {
            nitroPressed = false;
            model.stopNitro();
            e.consume();
            return true;
        }
        
        // Touche R pour réinitialiser
        if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_R) {
            model.resetVitesse();
            e.consume();
            return true;
        }
        
        // Touche D pour décélérer
        if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_D) {
            model.decelerer();
            e.consume();
            return true;
        }
        
        return false;
    }
    
    public void remove() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this);
    }
}
