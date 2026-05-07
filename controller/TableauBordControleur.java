package controller;

import modele.*;
import view.*;
import util.GlobalKeyHandler;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TableauBordControleur {
    private CompteurVitesseModel model;
    private JFrame frame;
    private JComboBox<Voiture> voitureSelector;
    private JComboBox<Piste> pisteSelector;
    private JLabel statusLabel;
    private PisteVue pisteVue;
    private ChronoVue chronoVue;
    private GlobalKeyHandler globalKeyHandler;
    private boolean finishRecorded = false;
    private static final String SAVE_FILE_NAME = "save.csv";
    
    public TableauBordControleur(CompteurVitesseModel model) {
        this.model = model;
        createAndShowGUI();
        // Initialiser le handler global des touches
        globalKeyHandler = new GlobalKeyHandler(model);
    }
    
    private void createAndShowGUI() {
        frame = new JFrame("Tableau de Bord Voiture - Avec Piste");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // Panel principal divisé
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        
        // Haut: Compteur de vitesse
        CompteurVitesseVue compteurVue = new CompteurVitesseVue(model);
        
        // Bas: Piste
        pisteVue = new PisteVue(model.getPisteActuelle());
        
        splitPane.setTopComponent(compteurVue);
        splitPane.setBottomComponent(pisteVue);
        splitPane.setResizeWeight(0.6);
        
        chronoVue = new ChronoVue();
        frame.add(chronoVue, BorderLayout.WEST);
        frame.add(splitPane, BorderLayout.CENTER);
        
        // Ajout des contrôles
        frame.add(createControlPanel(), BorderLayout.SOUTH);
        frame.add(createStatusPanel(), BorderLayout.NORTH);
        
        // Menu
        createMenuBar();
        
        // Mettre à jour la vue piste quand le modèle change
        model.addChangeListener(e -> {
            if (pisteVue != null && model.getPisteActuelle() != null) {
                pisteVue.setPiste(model.getPisteActuelle());
            }
            if (model.getPisteActuelle() != null) {
                double distance = model.getPisteActuelle().getDistanceParcourue();
                if (model.getPisteActuelle().estTerminee()) {
                    if (!finishRecorded) {
                        finishRecorded = true;
                        updateStatus("ARRIVEE ! La voiture a termine la piste !");
                        Toolkit.getDefaultToolkit().beep();
                        double tempsSecondes = model.getTempsCourseSecondesExact();
                        if (chronoVue != null) {
                            chronoVue.showFinishTimeSeconds(tempsSecondes);
                        }
                        enregistrerResultatCourse(tempsSecondes);
                    }
                } else if (distance == 0) {
                    finishRecorded = false;
                    if (chronoVue != null) {
                        chronoVue.clearFinish();
                    }
                }
            }
        });
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // Charger automatiquement les fichiers
        chargerFichierVoitures("voitures.csv");
        chargerFichierPiste("distance_piste.csv");
        
        // S'assurer que le frame a le focus pour les touches globales
        frame.requestFocus();
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Sélecteur de voiture
        JLabel carLabel = new JLabel("Voiture:");
        carLabel.setForeground(Color.WHITE);
        
        voitureSelector = new JComboBox<>();
        voitureSelector.setPreferredSize(new Dimension(200, 30));
        // Désactiver l'action par défaut de ESPACE sur la combo box
        disableSpaceOnComponent(voitureSelector);
        voitureSelector.addActionListener(e -> {
            if (voitureSelector.getSelectedItem() instanceof Voiture) {
                Voiture selected = (Voiture) voitureSelector.getSelectedItem();
                model.setVoiture(selected);
                updateStatus("Voiture sélectionnée: " + selected.getNom());
            }
        });
        
        // Sélecteur de piste
        JLabel pisteLabel = new JLabel("Piste:");
        pisteLabel.setForeground(Color.WHITE);
        
        pisteSelector = new JComboBox<>();
        pisteSelector.setPreferredSize(new Dimension(200, 30));
        // Désactiver l'action par défaut de ESPACE sur la combo box
        disableSpaceOnComponent(pisteSelector);
        pisteSelector.addActionListener(e -> {
            if (pisteSelector.getSelectedItem() instanceof Piste) {
                Piste selected = (Piste) pisteSelector.getSelectedItem();
                model.setPiste(selected);
                pisteVue.setPiste(selected);
                updateStatus("Piste sélectionnée: " + selected.getNom());
            }
        });
        
        // Boutons
        // JButton startBtn = createStyledButton("Accélerer (ESPACE)", new Color(0, 150, 0));
        // startBtn.addActionListener(e -> {
        //     if (model.getVoitureActuelle() != null && model.getPisteActuelle() != null) {
        //         if (model.getPisteActuelle().estTerminee()) {
        //             updateStatus("La piste est terminee! Utilisez R pour reinitialiser.");
        //         } else {
        //             model.startAcceleration();
        //         }
        //     } else {
        //         updateStatus("Selectionnez d'abord une voiture et une piste!");
        //     }
        // });
        
        // JButton stopBtn = createStyledButton("Arreter", new Color(150, 0, 0));
        // stopBtn.addActionListener(e -> model.stopCourse());
        
        JButton resetBtn = createStyledButton("Reinitialiser (R)", new Color(0, 0, 150));
        resetBtn.addActionListener(e -> {
            model.resetVitesse();
            updateStatus("Course réinitialisée!");
        });
        
        // JButton decelerateBtn = createStyledButton("Decelerer (D)", new Color(150, 150, 0));
        // decelerateBtn.addActionListener(e -> model.decelerer());
        
        panel.add(carLabel);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(voitureSelector);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(pisteLabel);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(pisteSelector);
        panel.add(Box.createHorizontalStrut(15));
        // panel.add(startBtn);
        // panel.add(Box.createHorizontalStrut(5));
        // panel.add(stopBtn);
        // panel.add(Box.createHorizontalStrut(5));
        // panel.add(decelerateBtn);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(resetBtn);
        
        return panel;
    }
    
    // Méthode pour désactiver l'action de la touche ESPACE sur un composant
    private void disableSpaceOnComponent(JComponent component) {
        // Remplacer l'action par défaut de ESPACE par une action vide
        InputMap inputMap = component.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = component.getActionMap();
        
        // Désactiver l'ouverture de la liste déroulante avec ESPACE
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");
        actionMap.put("none", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ne rien faire - ESPACE ne déclenche pas la sélection
            }
        });
        
        // S'assurer que le focus ne prend pas la touche ESPACE
        component.setFocusTraversalKeysEnabled(false);
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        statusLabel = new JLabel("_");
        statusLabel.setForeground(Color.LIGHT_GRAY);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        panel.add(statusLabel);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Désactiver la touche ESPACE sur les boutons
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
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("Fichier");
        
        JMenuItem loadCarItem = new JMenuItem("Charger voitures CSV");
        loadCarItem.addActionListener(e -> chargerFichierVoitures());
        
        JMenuItem loadTrackItem = new JMenuItem("Charger piste CSV");
        loadTrackItem.addActionListener(e -> chargerFichierPiste());
        
        JMenuItem exitItem = new JMenuItem("Quitter");
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(loadCarItem);
        fileMenu.add(loadTrackItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        JMenu helpMenu = new JMenu("Aide");
        JMenuItem aboutItem = new JMenuItem("À propos");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        frame.setJMenuBar(menuBar);
    }
    
    public void chargerFichierVoitures(String cheminFichier) {
        File file = new File(cheminFichier);
        if (file.exists()) {
            List<Voiture> voitures = parseVoituresCSV(file);
            if (!voitures.isEmpty()) {
                voitureSelector.removeAllItems();
                for (Voiture v : voitures) {
                    voitureSelector.addItem(v);
                }
                updateStatus("Chargé: " + voitures.size() + " voiture(s)");
                if (!voitures.isEmpty()) {
                    voitureSelector.setSelectedIndex(0);
                }
            }
        } else {
            updateStatus("Fichier non trouvé: " + cheminFichier);
        }
    }
    
    private void chargerFichierVoitures() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers CSV", "csv"));
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            List<Voiture> voitures = parseVoituresCSV(fileChooser.getSelectedFile());
            if (!voitures.isEmpty()) {
                voitureSelector.removeAllItems();
                for (Voiture v : voitures) {
                    voitureSelector.addItem(v);
                }
                updateStatus("Chargé: " + voitures.size() + " voiture(s)");
            }
        }
    }
    
    public void chargerFichierPiste(String cheminFichier) {
        File file = new File(cheminFichier);
        if (file.exists()) {
            List<Piste> pistes = parsePistesCSV(file);
            if (!pistes.isEmpty()) {
                pisteSelector.removeAllItems();
                for (Piste p : pistes) {
                    pisteSelector.addItem(p);
                }
                updateStatus("Chargé: " + pistes.size() + " piste(s)");
                if (!pistes.isEmpty()) {
                    pisteSelector.setSelectedIndex(0);
                }
            }
        } else {
            updateStatus("Fichier non trouvé: " + cheminFichier);
        }
    }
    
    private void chargerFichierPiste() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers CSV", "csv"));
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            List<Piste> pistes = parsePistesCSV(fileChooser.getSelectedFile());
            if (!pistes.isEmpty()) {
                pisteSelector.removeAllItems();
                for (Piste p : pistes) {
                    pisteSelector.addItem(p);
                }
                updateStatus("Chargé: " + pistes.size() + " piste(s)");
            }
        }
    }
    
    private List<Voiture> parseVoituresCSV(File file) {
        List<Voiture> voitures = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length >= 3) {
                    try {
                        String nom = data[0].trim();
                        double acceleration = Double.parseDouble(data[1].trim());
                        int vitesseMax = Integer.parseInt(data[2].trim());
                        double consoNitro = data.length > 3 ? parseDoubleOrZero(data[3]) : 0.0;
                        double variationAccel = data.length > 4 ? parseDoubleOrZero(data[4]) : 0.0;
                        double capaciteNitro = data.length > 5 ? parseDoubleOrZero(data[5]) : 0.0;
                        voitures.add(new Voiture(nom, acceleration, vitesseMax, consoNitro,
                                variationAccel, capaciteNitro));
                    } catch (NumberFormatException ex) {
                        System.err.println("Erreur de parsing: " + line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return voitures;
    }
    
    private List<Piste> parsePistesCSV(File file) {
        List<Piste> pistes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (line.trim().isEmpty()) continue;
                String[] data = line.contains(";") ? line.split(";") : line.split(",");
                if (data.length >= 2) {
                    try {
                        String nom;
                        String distanceText;
                        if (data.length >= 3
                                && isNumericToken(data[data.length - 2])
                                && isNumericToken(data[data.length - 1])) {
                            nom = String.join(",", Arrays.copyOf(data, data.length - 2)).trim();
                            distanceText = data[data.length - 2].trim() + "." + data[data.length - 1].trim();
                        } else {
                            nom = String.join(",", Arrays.copyOf(data, data.length - 1)).trim();
                            distanceText = data[data.length - 1].trim();
                        }
                        distanceText = distanceText.replace("km", "").replace("KM", "").replace("Km", "").trim();
                        distanceText = distanceText.replace(',', '.');
                        double distance = Double.parseDouble(distanceText);
                        pistes.add(new Piste(nom, distance));
                    } catch (NumberFormatException ex) {
                        System.err.println("Erreur de parsing: " + line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pistes;
    }

    private void enregistrerResultatCourse(double tempsSecondes) {
        Voiture voiture = model.getVoitureActuelle();
        if (voiture == null) {
            return;
        }
        double tempsSecurise = Math.max(0.0, tempsSecondes);
        Path savePath = Paths.get(SAVE_FILE_NAME);
        try {
            List<ResultatCourse> resultats = lireResultatsCourse(savePath);
            resultats.add(new ResultatCourse(voiture.getNom(), tempsSecurise));
            resultats.sort(Comparator.comparingDouble(r -> r.tempsSecondes));
            ecrireResultatsCourse(savePath, resultats);
            updateStatus("Resultat enregistre dans " + SAVE_FILE_NAME);
        } catch (IOException ex) {
            updateStatus("Erreur ecriture " + SAVE_FILE_NAME);
            ex.printStackTrace();
        }
    }

    private List<ResultatCourse> lireResultatsCourse(Path savePath) throws IOException {
        List<ResultatCourse> resultats = new ArrayList<>();
        if (!Files.exists(savePath)) {
            return resultats;
        }
        List<String> lines = Files.readAllLines(savePath, StandardCharsets.UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            if (i == 0 && line.toLowerCase().startsWith("voiture")) {
                continue;
            }
            String[] parts = line.split(",");
            if (parts.length < 2) {
                continue;
            }
            String nom = parts[0].trim();
            Double temps = parseDoubleOrNull(parts[1]);
            if (temps == null) {
                continue;
            }
            resultats.add(new ResultatCourse(nom, temps));
        }
        return resultats;
    }

    private void ecrireResultatsCourse(Path savePath, List<ResultatCourse> resultats) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("voiture,temps_s");
        for (ResultatCourse resultat : resultats) {
            lines.add(String.format(java.util.Locale.US, "%s,%.3f",
                    resultat.nomVoiture, resultat.tempsSecondes));
        }
        Files.write(savePath, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private boolean isNumericToken(String value) {
        String cleaned = value.trim().toLowerCase().replace("km", "").trim();
        if (cleaned.isEmpty()) {
            return false;
        }
        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            if (!Character.isDigit(c) && c != '.' && c != '-' && c != '+') {
                return false;
            }
        }
        return true;
    }

    private double parseDoubleOrZero(String value) {
        if (value == null) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private Double parseDoubleOrNull(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static class ResultatCourse {
        private final String nomVoiture;
        private final double tempsSecondes;

        private ResultatCourse(String nomVoiture, double tempsSecondes) {
            this.nomVoiture = nomVoiture;
            this.tempsSecondes = tempsSecondes;
        }
    }
    
    private void showAboutDialog() {
        String message = "Tableau de Bord Voiture - Version 3.1\n\n" +
                        "Fonctionnalités:\n" +
                        "• Compteur de vitesse avec aiguille\n" +
                        "• Piste de course de 40 km\n" +
                        "• Animation fluide de la voiture\n" +
                        "• Contrôle GLOBAL par ESPACE (n'affecte pas les autres composants)\n\n" +
                        "Contrôles:\n" +
                        "• ESPACE (maintenir) : Accélération GLOBALE\n" +
                        "• N (maintenir) : Nitro\n" +
                        "• R : Réinitialiser la course\n" +
                        "• D : Décélérer\n\n" +
                        "Fichiers requis:\n" +
                        "- voitures.csv (nom,accélération,vitesse_max,conso_nintro_kg/min,variation_acceleration_km/h/s/s,capaciter_nitro_kg)\n" +
                        "- distance_piste.csv (nom,distance_km)";
        
        JOptionPane.showMessageDialog(frame, message, "À propos", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            Timer timer = new Timer(3000, e -> {
                if (statusLabel != null && statusLabel.getText().equals(message)) {
                    statusLabel.setText("_");
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
}