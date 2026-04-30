import modele.CompteurVitesseModel;
import controller.TableauBordControleur;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CompteurVitesseModel model = new CompteurVitesseModel();
            TableauBordControleur controleur = new TableauBordControleur(model);
            
            // Charge automatiquement les fichiers CSV au démarrage
            // Note: Les méthodes ont changé de nom
            controleur.chargerFichierVoitures("voitures.csv");
            controleur.chargerFichierPiste("distance_piste.csv");
        });
    }
}