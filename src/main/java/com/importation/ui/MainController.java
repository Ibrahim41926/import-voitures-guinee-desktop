package com.importation.ui;

import com.importation.models.Associe;
import com.importation.models.Voiture;
import com.importation.models.dao.AssocieDAO;
import com.importation.models.dao.VoitureDAO;
import com.importation.models.dao.controllers.AssocieController;
import com.importation.models.dao.controllers.VoitureController;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.transform.Scale;
import javafx.print.PrinterJob;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.scene.control.TableCell;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.filechooser.FileSystemView;

import com.importation.models.dao.DatabaseConnection;
import com.importation.models.dao.controllers.utils.Constantes;
import com.importation.models.dao.controllers.utils.ConvertisseurDevise;

import java.awt.Desktop;
import java.io.IOException;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ContrÃ´leur principal de l'interface
 */
public class MainController {
    
    @FXML
    private StackPane mainContent;
    
    @FXML
    private VBox tableauBordPane;
    
    @FXML
    private VBox voituresPane;
    
    @FXML
    private VBox associesPane;
    
    @FXML
    private VBox tauxChangePane;

    @FXML
    private VBox baseDonneesPane;
    
    @FXML
    private Label statusLabel;

    @FXML
    private Label tauxActuelLabel;

    @FXML
    private TextField nouveauTauxField;

    @FXML
    private TextArea sqlQueryArea;

    @FXML
    private TextArea sqlResultArea;

    @FXML
    private TableView<Voiture> dashboardTable;

    @FXML
    private TableColumn<Voiture, Integer> colId;

    @FXML
    private TableColumn<Voiture, String> colMarque;

    @FXML
    private TableColumn<Voiture, String> colModele;

    @FXML
    private TableColumn<Voiture, Integer> colAnnee;

    @FXML
    private TableColumn<Voiture, String> colImmatriculation;

    @FXML
    private TableColumn<Voiture, Double> colPrixAchatCad;

    @FXML
    private TableColumn<Voiture, Double> colTransportCad;

    @FXML
    private TableColumn<Voiture, Double> colAssuranceCad;

    @FXML
    private TableColumn<Voiture, Double> colDedouanementGnf;

    @FXML
    private TableColumn<Voiture, Double> colFraisDiversCad;

    @FXML
    private TableColumn<Voiture, Double> colFraisDiversGnf;

    @FXML
    private TableColumn<Voiture, Double> colCoutTotalGnf;

    @FXML
    private TableColumn<Voiture, Double> colPrixReventeGnf;

    @FXML
    private TableColumn<Voiture, String> colDateImportation;

    @FXML
    private TableColumn<Voiture, String> colStatut;

    @FXML
    private TableColumn<Voiture, String> colAssocie;

    @FXML
    private Label kpiTotalVoitures;

    @FXML
    private Label kpiVoituresVendues;

    @FXML
    private Label kpiTotalAssocies;

    @FXML
    private Label kpiTotalContributions;

    @FXML
    private Button navDashboardButton;

    @FXML
    private Button navVoituresButton;

    @FXML
    private Button navAssociesButton;

    @FXML
    private Button navTauxButton;

    @FXML
    private Button navBaseButton;
    
    @FXML
    public void initialize() {
        try {
            ConvertisseurDevise.initialiserDepuisBase();
        } catch (SQLException e) {
            // Le taux par defaut reste utilise si le chargement echoue.
        }

        // Charger les FXML des sous-vues
        configurerTableauBord();
        chargerVoitures();
        chargerAssocies();
        chargerTableauBord();
        chargerIndicateurs();
        rafraichirTauxChange();
        afficherTableauBord();
    }

    private void afficherPaneUnique(VBox pane) {
        tableauBordPane.setVisible(false);
        tableauBordPane.setManaged(false);
        voituresPane.setVisible(false);
        voituresPane.setManaged(false);
        associesPane.setVisible(false);
        associesPane.setManaged(false);
        tauxChangePane.setVisible(false);
        tauxChangePane.setManaged(false);
        baseDonneesPane.setVisible(false);
        baseDonneesPane.setManaged(false);

        pane.setVisible(true);
        pane.setManaged(true);
        pane.toFront();
    }

    private void mettreEnAvantNavigation(Button boutonActif) {
        Button[] boutons = {
            navDashboardButton,
            navVoituresButton,
            navAssociesButton,
            navTauxButton,
            navBaseButton
        };

        for (Button bouton : boutons) {
            if (bouton != null) {
                bouton.getStyleClass().remove("nav-button-active");
            }
        }

        if (boutonActif != null && !boutonActif.getStyleClass().contains("nav-button-active")) {
            boutonActif.getStyleClass().add("nav-button-active");
        }
    }

    private void configurerTableauBord() {
        colId.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getId()).asObject());
        colMarque.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMarque()));
        colModele.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getModele()));
        colAnnee.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getAnnee()).asObject());
        colImmatriculation.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getImmatriculation()));
        colPrixAchatCad.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrixAchatCAD()).asObject());
        colTransportCad.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getTransportCAD()).asObject());
        colAssuranceCad.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getAssuranceCAD()).asObject());
        colDedouanementGnf.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getDedouanementGNF()).asObject());
        colFraisDiversCad.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getFraisDiversCAD()).asObject());
        colFraisDiversGnf.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getFraisDiversGNF()).asObject());
        colCoutTotalGnf.setCellValueFactory(cell -> new SimpleDoubleProperty(
            VoitureController.calculerCoutTotal(cell.getValue())
        ).asObject());
        colPrixReventeGnf.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrixReventeGNF()).asObject());
        colDateImportation.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().getDateImportation() == null ? "" : cell.getValue().getDateImportation().toString())
        );
        colStatut.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatut()));
        colAssocie.setCellValueFactory(cell -> new SimpleStringProperty(""));

        // Rend les montants lisibles avec 2 decimales.
        setDoubleCellFactory(colPrixAchatCad);
        setDoubleCellFactory(colTransportCad);
        setDoubleCellFactory(colAssuranceCad);
        setDoubleCellFactory(colDedouanementGnf);
        setDoubleCellFactory(colFraisDiversCad);
        setDoubleCellFactory(colFraisDiversGnf);
        setDoubleCellFactory(colCoutTotalGnf);
        setPrixReventeCellFactory();
    }

    private void setDoubleCellFactory(TableColumn<Voiture, Double> column) {
        column.setCellFactory(c -> new TableCell<Voiture, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format(Locale.US, "%,.2f", item));
                }
            }
        });
    }

    private void setCurrencyCellFactory(TableColumn<Voiture, Double> column, String devise) {
        column.setCellFactory(c -> new TableCell<Voiture, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(devise + " " + String.format(Locale.US, "%,.0f", item));
                }
            }
        });
    }

    private void setPrixReventeCellFactory() {
        colPrixReventeGnf.setCellFactory(c -> new TableCell<Voiture, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    return;
                }

                Voiture voiture = getTableRow().getItem();
                if (estStatutVendu(voiture.getStatut())) {
                    setText(String.format(Locale.US, "%,.2f", item));
                } else {
                    setText("");
                }
            }
        });
    }

    private boolean estStatutVendu(String statut) {
        if (statut == null) {
            return false;
        }
        String s = statut.trim().toUpperCase(Locale.ROOT);
        return "VENDUE".equals(s) || "VENDU".equals(s);
    }

    private void chargerTableauBord() {
        try {
            List<Voiture> voitures = VoitureDAO.obtenirTous();
            Map<Integer, String> associesParId = chargerAssociesParId();

            colAssocie.setCellValueFactory(cell -> {
                int associeId = cell.getValue().getAssocieId();
                String nomAssocie = associesParId.getOrDefault(associeId, "");
                return new SimpleStringProperty(nomAssocie);
            });

            dashboardTable.setItems(FXCollections.observableArrayList(voitures));
        } catch (SQLException e) {
            statusLabel.setText("Erreur chargement tableau de bord: " + e.getMessage());
        }
    }

    private void chargerIndicateurs() {
        try {
            List<Voiture> voitures = VoitureDAO.obtenirTous();
            List<Associe> associes = AssocieDAO.obtenirTous();

            long nbVendues = voitures.stream()
                .filter(v -> "VENDUE".equalsIgnoreCase(v.getStatut()))
                .count();

            double totalContributions = 0.0;
            for (Associe associe : associes) {
                totalContributions += AssocieController.calculerTotalContributions(associe.getId());
            }

            kpiTotalVoitures.setText(String.valueOf(voitures.size()));
            kpiVoituresVendues.setText(String.valueOf(nbVendues));
            kpiTotalAssocies.setText(String.valueOf(associes.size()));
            kpiTotalContributions.setText(String.format(Locale.US, "%,.0f GNF", totalContributions));
        } catch (SQLException e) {
            kpiTotalVoitures.setText("-");
            kpiVoituresVendues.setText("-");
            kpiTotalAssocies.setText("-");
            kpiTotalContributions.setText("-");
        }
    }

    private Map<Integer, String> chargerAssociesParId() throws SQLException {
        List<Associe> associes = AssocieDAO.obtenirTous();
        Map<Integer, String> map = new HashMap<>();
        for (Associe associe : associes) {
            map.put(associe.getId(), associe.getPrenom() + " " + associe.getNom());
        }
        return map;
    }
    
    private void chargerVoitures() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/importation/models/resources/fxml/voiture.fxml"));
            Parent voitureUI = loader.load();
            voituresPane.getChildren().add(voitureUI);
        } catch (IOException e) {
            e.printStackTrace();
            voituresPane.getChildren().add(new Label("Erreur chargement interface voitures"));
        }
    }
    
    private void chargerAssocies() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/importation/models/resources/fxml/associe.fxml"));
            Parent associeUI = loader.load();
            associesPane.getChildren().add(associeUI);
        } catch (IOException e) {
            e.printStackTrace();
            associesPane.getChildren().add(new Label("Erreur chargement interface associÃ©s"));
        }
    }
    
    @FXML
    public void afficherTableauBord() {
        statusLabel.setText("Tableau de bord");
        chargerTableauBord();
        chargerIndicateurs();
        mettreEnAvantNavigation(navDashboardButton);
        afficherPaneUnique(tableauBordPane);
    }
    
    @FXML
    public void afficherVoitures() {
        statusLabel.setText("Gestion des voitures");
        mettreEnAvantNavigation(navVoituresButton);
        afficherPaneUnique(voituresPane);
    }
    
    @FXML
    public void afficherAssocies() {
        statusLabel.setText("Gestion des associÃ©s");
        mettreEnAvantNavigation(navAssociesButton);
        afficherPaneUnique(associesPane);
    }
    
    @FXML
    public void afficherTauxChange() {
        statusLabel.setText("Gestion du taux de change");
        rafraichirTauxChange();
        mettreEnAvantNavigation(navTauxButton);
        afficherPaneUnique(tauxChangePane);
    }

    @FXML
    public void afficherBaseDonnees() {
        statusLabel.setText("Gestion base de donnees");
        mettreEnAvantNavigation(navBaseButton);
        afficherPaneUnique(baseDonneesPane);
    }

    @FXML
    public void executerSql() {
        String requete = sqlQueryArea.getText();
        if (requete == null || requete.trim().isEmpty()) {
            statusLabel.setText("Saisissez une requete SQL");
            return;
        }

        String sql = requete.trim();
        String sqlUpper = sql.toUpperCase(Locale.ROOT);
        boolean estLecture = sqlUpper.startsWith("SELECT")
            || sqlUpper.startsWith("PRAGMA")
            || sqlUpper.startsWith("WITH");

        try {
            Connection connexion = DatabaseConnection.obtenirConnexion();
            try (Statement statement = connexion.createStatement()) {
                if (estLecture) {
                    try (ResultSet resultSet = statement.executeQuery(sql)) {
                        sqlResultArea.setText(formaterResultatSql(resultSet));
                    }
                    statusLabel.setText("Requete executee (lecture)");
                } else {
                    int lignes = statement.executeUpdate(sql);
                    sqlResultArea.setText("Commande executee. Lignes affectees: " + lignes);
                    statusLabel.setText("Commande executee (ecriture)");
                    chargerTableauBord();
                    chargerIndicateurs();
                }
            }
        } catch (SQLException e) {
            sqlResultArea.setText("Erreur SQL: " + e.getMessage());
            statusLabel.setText("Erreur SQL");
        }
    }

    @FXML
    public void effacerSql() {
        sqlQueryArea.clear();
        sqlResultArea.clear();
        statusLabel.setText("Zone SQL effacee");
    }

    private String formaterResultatSql(ResultSet resultSet) throws SQLException {
        StringBuilder sb = new StringBuilder();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int nbColonnes = metaData.getColumnCount();

        for (int i = 1; i <= nbColonnes; i++) {
            if (i > 1) {
                sb.append(" | ");
            }
            sb.append(metaData.getColumnLabel(i));
        }
        sb.append('\n');

        int nbLignes = 0;
        while (resultSet.next()) {
            nbLignes++;
            for (int i = 1; i <= nbColonnes; i++) {
                if (i > 1) {
                    sb.append(" | ");
                }
                Object valeur = resultSet.getObject(i);
                sb.append(valeur == null ? "NULL" : valeur.toString());
            }
            sb.append('\n');
        }

        if (nbLignes == 0) {
            sb.append("(Aucun resultat)");
        } else {
            sb.append("\nTotal lignes: ").append(nbLignes);
        }
        return sb.toString();
    }

    @FXML
    public void appliquerNouveauTaux() {
        String valeurSaisie = nouveauTauxField.getText();
        if (valeurSaisie == null || valeurSaisie.trim().isEmpty()) {
            statusLabel.setText("Saisissez un taux valide");
            return;
        }

        try {
            double nouveauTaux = Double.parseDouble(valeurSaisie.trim().replace(',', '.'));
            ConvertisseurDevise.setTauxChangeEtSauvegarder(nouveauTaux);
            rafraichirTauxChange();
            chargerTableauBord();
            chargerIndicateurs();
            nouveauTauxField.clear();
            statusLabel.setText("Nouveau taux applique: 1 CAD = " + String.format(Locale.US, "%,.2f", nouveauTaux) + " GNF");
        } catch (NumberFormatException e) {
            statusLabel.setText("Format invalide. Exemple: 6400");
        } catch (SQLException e) {
            statusLabel.setText("Erreur sauvegarde taux: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            statusLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void reinitialiserTauxDefaut() {
        try {
            ConvertisseurDevise.setTauxChangeEtSauvegarder(Constantes.TAUX_CHANGE_DEFAUT);
            rafraichirTauxChange();
            chargerTableauBord();
            chargerIndicateurs();
            nouveauTauxField.clear();
            statusLabel.setText("Taux reinitialise: 1 CAD = " + String.format(Locale.US, "%,.2f", Constantes.TAUX_CHANGE_DEFAUT) + " GNF");
        } catch (SQLException e) {
            statusLabel.setText("Erreur sauvegarde taux: " + e.getMessage());
        }
    }

    private void rafraichirTauxChange() {
        if (tauxActuelLabel != null) {
            tauxActuelLabel.setText(
                "1 CAD = " + String.format(Locale.US, "%,.2f", ConvertisseurDevise.getTauxChange()) + " GNF"
            );
        }
    }

    @FXML
    public void modifierVoitureDashboard() {
        Voiture selection = dashboardTable.getSelectionModel().getSelectedItem();
        if (selection == null) {
            statusLabel.setText("Selectionnez une voiture a modifier");
            return;
        }
        ouvrirPopupVoiture(selection);
    }

    @FXML
    public void supprimerVoitureDashboard() {
        Voiture selection = dashboardTable.getSelectionModel().getSelectedItem();
        if (selection == null) {
            statusLabel.setText("Selectionnez une voiture a supprimer");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la voiture selectionnee ?");
        confirm.setContentText(selection.getMarque() + " " + selection.getModele());
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            VoitureDAO.supprimer(selection.getId());
            chargerTableauBord();
            chargerIndicateurs();
            statusLabel.setText("Voiture supprimee");
        } catch (SQLException e) {
            statusLabel.setText("Erreur suppression: " + e.getMessage());
        }
    }

    @FXML
    public void exporterTableauBordPdf() {
        File fichier = null;
        try {
            List<Voiture> voitures = new ArrayList<>(dashboardTable.getItems());
            if (voitures.isEmpty()) {
                statusLabel.setText("Aucune voiture a exporter");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter le rapport PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
            File dossierInitial = determinerDossierExportPdf();
            if (dossierInitial != null) {
                fileChooser.setInitialDirectory(dossierInitial);
            }
            fileChooser.setInitialFileName(
                "rapport-vehicules-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")) + ".pdf"
            );

            fichier = preparerFichierPdf(fileChooser.showSaveDialog(mainContent.getScene().getWindow()));
            if (fichier == null) {
                statusLabel.setText("Export annule");
                return;
            }

            PdfReportExporter.export(fichier.toPath(), voitures, ConvertisseurDevise.getTauxChange());
            statusLabel.setText("Rapport PDF genere: " + fichier.getAbsolutePath());
            afficherSuccesExportPdf(fichier);
        } catch (Exception e) {
            statusLabel.setText("Erreur export PDF: " + e.getMessage());
            afficherErreurExportPdf(fichier, e);
        }
    }

    private File preparerFichierPdf(File fichierSelectionne) {
        if (fichierSelectionne == null) {
            return null;
        }

        String nom = fichierSelectionne.getName();
        if (nom.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            return fichierSelectionne;
        }

        File parent = fichierSelectionne.getParentFile();
        return new File(parent, nom + ".pdf");
    }

    private File determinerDossierExportPdf() {
        File dossierParDefaut = FileSystemView.getFileSystemView().getDefaultDirectory();
        if (estDossierAccessible(dossierParDefaut)) {
            return dossierParDefaut;
        }

        Path[] candidats = new Path[] {
            Path.of(System.getProperty("user.home"), "Downloads"),
            Path.of(System.getProperty("user.home"), "Documents"),
            Path.of(System.getProperty("user.home")),
            Path.of(".").toAbsolutePath().normalize()
        };

        for (Path candidat : candidats) {
            File dossier = candidat.toFile();
            if (estDossierAccessible(dossier)) {
                return dossier;
            }
        }

        return null;
    }

    private boolean estDossierAccessible(File dossier) {
        return dossier != null && dossier.exists() && dossier.isDirectory() && dossier.canWrite();
    }

    private void afficherErreurExportPdf(File fichier, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur export PDF");
        alert.setHeaderText("Impossible d'enregistrer le document PDF.");

        String chemin = fichier == null ? "(aucun chemin selectionne)" : fichier.getAbsolutePath();
        String cause = e.getMessage() == null || e.getMessage().isBlank()
            ? e.getClass().getSimpleName()
            : e.getMessage();

        alert.setContentText(
            "Chemin cible: " + chemin
                + "\nCause: " + cause
                + "\nConseil: essaie d'enregistrer dans Documents ou Downloads et ferme le PDF s'il est deja ouvert."
        );

        if (mainContent != null && mainContent.getScene() != null) {
            alert.initOwner(mainContent.getScene().getWindow());
        }
        alert.showAndWait();
    }

    private void afficherSuccesExportPdf(File fichier) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rapport PDF genere");
        alert.setHeaderText("Le rapport PDF a bien ete enregistre.");

        String chemin = fichier.getAbsolutePath();
        boolean desktopDisponible = Desktop.isDesktopSupported();
        boolean ouvertureFichierDisponible = desktopDisponible && Desktop.getDesktop().isSupported(Desktop.Action.OPEN);

        ButtonType ouvrirPdf = new ButtonType("Ouvrir le PDF");
        ButtonType ouvrirDossier = new ButtonType("Ouvrir le dossier");
        ButtonType fermer = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);

        if (ouvertureFichierDisponible) {
            alert.getButtonTypes().setAll(ouvrirPdf, ouvrirDossier, fermer);
        } else {
            alert.getButtonTypes().setAll(fermer);
        }

        alert.setContentText(
            "Chemin du fichier:\n" + chemin
                + "\n\nTu peux retrouver le PDF dans ce dossier, ou l'ouvrir directement depuis cette boite."
        );

        if (mainContent != null && mainContent.getScene() != null) {
            alert.initOwner(mainContent.getScene().getWindow());
        }

        Optional<ButtonType> choix = alert.showAndWait();
        if (choix.isEmpty()) {
            return;
        }

        try {
            if (choix.get() == ouvrirPdf) {
                Desktop.getDesktop().open(fichier);
            } else if (choix.get() == ouvrirDossier) {
                Desktop.getDesktop().open(fichier.getParentFile());
            }
        } catch (Exception e) {
            statusLabel.setText("PDF cree mais ouverture impossible: " + e.getMessage());
        }
    }

    private ResumeExportPdf calculerResumeExport(List<Voiture> voitures) {
        long totalVendues = 0;
        double totalAchatCad = 0.0;
        double totalCoutGnf = 0.0;
        double totalVenteGnf = 0.0;
        double margeVenduesGnf = 0.0;

        for (Voiture voiture : voitures) {
            totalAchatCad += voiture.getPrixAchatCAD();
            double coutTotal = VoitureController.calculerCoutTotal(voiture);
            totalCoutGnf += coutTotal;

            if (estStatutVendu(voiture.getStatut())) {
                totalVendues++;
                totalVenteGnf += voiture.getPrixReventeGNF();
                margeVenduesGnf += voiture.getPrixReventeGNF() - coutTotal;
            }
        }

        return new ResumeExportPdf(
            voitures.size(),
            totalVendues,
            totalAchatCad,
            totalCoutGnf,
            totalVenteGnf,
            margeVenduesGnf
        );
    }

    private List<List<Voiture>> decouperVoituresPourRapport(List<Voiture> voitures, int lignesPremierePage, int lignesPagesSuivantes) {
        List<List<Voiture>> pages = new ArrayList<>();
        int index = 0;
        boolean premierePage = true;

        while (index < voitures.size()) {
            int tailleBloc = premierePage ? lignesPremierePage : lignesPagesSuivantes;
            int fin = Math.min(index + tailleBloc, voitures.size());
            pages.add(new ArrayList<>(voitures.subList(index, fin)));
            index = fin;
            premierePage = false;
        }

        return pages;
    }

    private VBox creerPageRapportPdf(
        List<Voiture> pageItems,
        ResumeExportPdf resume,
        String numeroRapport,
        String dateExport,
        int pageCourante,
        int totalPages,
        boolean premierePage
    ) {
        VBox page = new VBox(16);
        page.getStyleClass().add("print-page");

        ImageView logo = creerLogoView();
        logo.setFitHeight(54);
        logo.setFitWidth(150);

        Label kicker = new Label("PORTEFEUILLE IMPORTATION");
        kicker.getStyleClass().add("report-kicker");
        Label titre = new Label("Rapport vehicules");
        titre.getStyleClass().add("report-title");
        Label sousTitre = new Label("Synthese d inventaire, couts et ventes");
        sousTitre.getStyleClass().add("report-subtitle");

        VBox blocTitre = new VBox(4, kicker, titre, sousTitre);
        blocTitre.setAlignment(Pos.CENTER_LEFT);

        Label rapport = new Label("Rapport " + numeroRapport);
        rapport.getStyleClass().add("report-meta-strong");
        Label dateLabel = new Label("Genere le " + dateExport);
        dateLabel.getStyleClass().add("report-meta");
        Label taux = new Label("Taux applique: 1 CAD = " + String.format(Locale.US, "%,.2f", ConvertisseurDevise.getTauxChange()) + " GNF");
        taux.getStyleClass().add("report-meta");
        Label pagination = new Label("Page " + pageCourante + " / " + totalPages);
        pagination.getStyleClass().add("report-meta");

        VBox blocMeta = new VBox(5, rapport, dateLabel, taux, pagination);
        blocMeta.setAlignment(Pos.CENTER_RIGHT);
        blocMeta.setMinWidth(220);

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        HBox hero = new HBox(18, logo, blocTitre, espace, blocMeta);
        hero.setAlignment(Pos.CENTER_LEFT);
        hero.getStyleClass().add("report-hero");
        page.getChildren().add(hero);

        if (premierePage) {
            FlowPane grille = new FlowPane(12, 12);
            grille.getStyleClass().add("metric-grid");
            grille.getChildren().addAll(
                creerCarteSynthese("Vehicules exportes", String.valueOf(resume.totalVehicules()), "Nombre de lignes incluses dans le rapport", "metric-card-ink"),
                creerCarteSynthese("Vehicules vendues", String.valueOf(resume.totalVendues()), "Unites vendues sur l ensemble exporte", "metric-card-teal"),
                creerCarteSynthese("Total achat", formatCadPdf(resume.totalAchatCad()), "Montant cumule des achats en CAD", "metric-card-copper"),
                creerCarteSynthese("Cout total", formatGnfPdf(resume.totalCoutGnf()), "Cout complet converti en GNF", "metric-card-amber"),
                creerCarteSynthese("Marge realisee", formatGnfPdf(resume.margeVenduesGnf()), "Ventes vendues moins couts complets", "metric-card-teal")
            );
            page.getChildren().add(grille);
        }

        Label sectionTitre = new Label(premierePage ? "Detail des vehicules" : "Suite du detail des vehicules");
        sectionTitre.getStyleClass().add("report-section-title");
        Label sectionNote = new Label(
            premierePage
                ? "Vue de lecture rapide avec les colonnes les plus utiles au pilotage."
                : "Continuation de l inventaire exporte."
        );
        sectionNote.getStyleClass().add("report-section-note");

        VBox sectionTable = new VBox(8, sectionTitre, sectionNote, creerTableauExportPdf(pageItems));
        sectionTable.getStyleClass().add("report-section");
        page.getChildren().add(sectionTable);

        Label footerGauche = new Label("Import Voitures Guinee");
        footerGauche.getStyleClass().add("report-footer-text");
        Label footerCentre = new Label("Document interne");
        footerCentre.getStyleClass().add("report-footer-text");
        Label footerDroite = new Label("Page " + pageCourante + " / " + totalPages);
        footerDroite.getStyleClass().add("report-footer-text");

        Region footerSpace1 = new Region();
        Region footerSpace2 = new Region();
        HBox.setHgrow(footerSpace1, Priority.ALWAYS);
        HBox.setHgrow(footerSpace2, Priority.ALWAYS);

        HBox footer = new HBox(8, footerGauche, footerSpace1, footerCentre, footerSpace2, footerDroite);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.getStyleClass().add("report-footer");
        page.getChildren().add(footer);

        return page;
    }

    private VBox creerCarteSynthese(String titre, String valeur, String detail, String ton) {
        Label titreLabel = new Label(titre);
        titreLabel.getStyleClass().add("metric-label");

        Label valeurLabel = new Label(valeur);
        valeurLabel.getStyleClass().add("metric-value");

        Label detailLabel = new Label(detail);
        detailLabel.getStyleClass().add("metric-detail");
        detailLabel.setWrapText(true);

        VBox carte = new VBox(4, titreLabel, valeurLabel, detailLabel);
        carte.getStyleClass().setAll("metric-card", ton);
        carte.setPrefWidth(175);
        return carte;
    }

    private TableView<Voiture> creerTableauExportPdf(List<Voiture> voitures) {
        TableView<Voiture> table = new TableView<>(FXCollections.observableArrayList(voitures));
        table.getStyleClass().setAll("table-view", "report-table");
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.setSelectionModel(null);
        table.setFocusTraversable(false);
        table.setFixedCellSize(30);

        TableColumn<Voiture, String> cReference = new TableColumn<>("Ref");
        cReference.setPrefWidth(82);
        cReference.setCellValueFactory(cell -> new SimpleStringProperty(formaterReferencePdf(cell.getValue())));

        TableColumn<Voiture, String> cVehicule = new TableColumn<>("Vehicule");
        cVehicule.setPrefWidth(175);
        cVehicule.setCellValueFactory(cell -> new SimpleStringProperty(formaterVehiculePdf(cell.getValue())));

        TableColumn<Voiture, String> cImport = new TableColumn<>("Import");
        cImport.setPrefWidth(86);
        cImport.setCellValueFactory(cell -> new SimpleStringProperty(formaterDatePdf(cell.getValue())));

        TableColumn<Voiture, String> cStatut = new TableColumn<>("Statut");
        cStatut.setPrefWidth(86);
        cStatut.setCellValueFactory(cell -> new SimpleStringProperty(formaterStatutPdf(cell.getValue())));
        cStatut.setCellFactory(column -> new TableCell<Voiture, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(item);
                Voiture voiture = getTableRow() == null ? null : getTableRow().getItem();
                if (voiture != null && estStatutVendu(voiture.getStatut())) {
                    setStyle("-fx-text-fill: #166534; -fx-font-weight: 800;");
                } else {
                    setStyle("-fx-text-fill: #9a6700; -fx-font-weight: 700;");
                }
            }
        });

        TableColumn<Voiture, String> cAchat = new TableColumn<>("Achat CAD");
        cAchat.setPrefWidth(100);
        cAchat.setCellValueFactory(cell -> new SimpleStringProperty(formatCadPdf(cell.getValue().getPrixAchatCAD())));

        TableColumn<Voiture, String> cCout = new TableColumn<>("Cout GNF");
        cCout.setPrefWidth(100);
        cCout.setCellValueFactory(cell -> new SimpleStringProperty(formatGnfPdf(VoitureController.calculerCoutTotal(cell.getValue()))));

        TableColumn<Voiture, String> cVente = new TableColumn<>("Vente GNF");
        cVente.setPrefWidth(100);
        cVente.setCellValueFactory(cell -> new SimpleStringProperty(formaterVentePdf(cell.getValue())));

        TableColumn<Voiture, String> cMarge = new TableColumn<>("Marge GNF");
        cMarge.setPrefWidth(104);
        cMarge.setCellValueFactory(cell -> new SimpleStringProperty(formaterMargePdf(cell.getValue())));
        cMarge.setCellFactory(column -> new TableCell<Voiture, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(item);
                Voiture voiture = getTableRow() == null ? null : getTableRow().getItem();
                if (voiture == null || !estStatutVendu(voiture.getStatut())) {
                    setStyle("-fx-text-fill: #64748b;");
                    return;
                }

                double marge = voiture.getPrixReventeGNF() - VoitureController.calculerCoutTotal(voiture);
                if (marge >= 0) {
                    setStyle("-fx-text-fill: #166534; -fx-font-weight: 800;");
                } else {
                    setStyle("-fx-text-fill: #b42318; -fx-font-weight: 800;");
                }
            }
        });

        table.getColumns().addAll(cReference, cVehicule, cImport, cStatut, cAchat, cCout, cVente, cMarge);

        double largeurColonnes = 0.0;
        for (TableColumn<Voiture, ?> colonne : table.getColumns()) {
            largeurColonnes += colonne.getPrefWidth();
        }

        double hauteurHeader = 34.0;
        double hauteurTotale = hauteurHeader + (voitures.size() * table.getFixedCellSize()) + 2.0;
        table.setPrefWidth(largeurColonnes + 2.0);
        table.setMinWidth(largeurColonnes + 2.0);
        table.setMaxWidth(largeurColonnes + 2.0);
        table.setPrefHeight(hauteurTotale);
        table.setMinHeight(hauteurTotale);
        table.setMaxHeight(hauteurTotale);

        return table;
    }

    private boolean imprimerPageRapport(PrinterJob job, PageLayout pageLayout, VBox contenu, String styleImpression) {
        Group printRoot = new Group(contenu);
        Scene printScene = new Scene(printRoot);
        if (styleImpression != null) {
            printScene.getStylesheets().add(styleImpression);
        }

        contenu.applyCss();
        contenu.layout();
        printRoot.applyCss();
        printRoot.layout();

        double largeurZone = contenu.getBoundsInLocal().getWidth();
        double hauteurZone = contenu.getBoundsInLocal().getHeight();
        double largeurImprimable = pageLayout.getPrintableWidth();
        double hauteurImprimable = pageLayout.getPrintableHeight();

        double scaleX = largeurImprimable / largeurZone;
        double scaleY = hauteurImprimable / hauteurZone;
        double facteur = Math.min(1.0, Math.min(scaleX, scaleY));

        Scale scale = new Scale(facteur, facteur);
        printRoot.getTransforms().add(scale);
        boolean imprime = job.printPage(pageLayout, printRoot);
        printRoot.getTransforms().remove(scale);
        return imprime;
    }

    private String chargerFeuilleStyleImpression() {
        try {
            return getClass()
                .getResource("/com/importation/models/resources/fxml/css/print-report.css")
                .toExternalForm();
        } catch (Exception e) {
            return null;
        }
    }

    private String formaterReferencePdf(Voiture voiture) {
        String immatriculation = voiture.getImmatriculation();
        if (immatriculation == null || immatriculation.isBlank()) {
            return "ID-" + voiture.getId();
        }
        return immatriculation;
    }

    private String formaterVehiculePdf(Voiture voiture) {
        return voiture.getMarque() + " " + voiture.getModele() + " (" + voiture.getAnnee() + ")";
    }

    private String formaterDatePdf(Voiture voiture) {
        if (voiture.getDateImportation() == null) {
            return "-";
        }
        return voiture.getDateImportation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String formaterStatutPdf(Voiture voiture) {
        if (voiture.getStatut() == null || voiture.getStatut().isBlank()) {
            return "-";
        }
        return voiture.getStatut().replace('_', ' ');
    }

    private String formaterVentePdf(Voiture voiture) {
        if (!estStatutVendu(voiture.getStatut())) {
            return "-";
        }
        return formatGnfPdf(voiture.getPrixReventeGNF());
    }

    private String formaterMargePdf(Voiture voiture) {
        if (!estStatutVendu(voiture.getStatut())) {
            return "-";
        }
        return formatGnfPdf(voiture.getPrixReventeGNF() - VoitureController.calculerCoutTotal(voiture));
    }

    private String formatCadPdf(double montant) {
        return "CAD " + String.format(Locale.US, "%,.2f", montant);
    }

    private String formatGnfPdf(double montant) {
        return "GNF " + String.format(Locale.US, "%,.0f", montant);
    }

    private record ResumeExportPdf(
        int totalVehicules,
        long totalVendues,
        double totalAchatCad,
        double totalCoutGnf,
        double totalVenteGnf,
        double margeVenduesGnf
    ) {}

    private ImageView creerLogoView() {
        Image image = chargerLogo();
        if (image == null) {
            return new ImageView();
        }
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(72);
        imageView.setFitWidth(180);
        return imageView;
    }

    private Image chargerLogo() {
        try {
            Path[] candidats = new Path[] {
                Path.of("src", "main", "images", "LOGO.png"),
                Path.of("src", "main", "images", "logo.png"),
                Path.of("images", "LOGO.png"),
                Path.of("images", "logo.png")
            };
            for (Path p : candidats) {
                if (Files.exists(p)) {
                    try (InputStream is = Files.newInputStream(p)) {
                        return new Image(is);
                    }
                }
            }
        } catch (Exception ignored) {
            // Fallback ci-dessous.
        }

        try {
            InputStream is = getClass().getResourceAsStream("/LOGO.png");
            if (is != null) {
                return new Image(is);
            }
        } catch (Exception ignored) {
            // Aucun logo charge.
        }
        return null;
    }

    private void ouvrirPopupVoiture(Voiture voiture) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/importation/models/resources/fxml/voiture_form.fxml")
            );
            Parent root = loader.load();
            VoitureFormController formController = loader.getController();
            formController.setVoiture(voiture);
            formController.setOnSaved(() -> {
                chargerTableauBord();
                chargerIndicateurs();
            });

            Stage stage = new Stage();
            stage.setTitle("Modifier voiture");
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            String css = getClass().getResource("/com/importation/models/resources/fxml/css/style.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            statusLabel.setText("Erreur ouverture formulaire: " + e.getMessage());
        }
    }
}
