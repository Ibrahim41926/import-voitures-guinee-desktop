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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.importation.models.dao.DatabaseConnection;
import com.importation.models.dao.controllers.utils.Constantes;
import com.importation.models.dao.controllers.utils.ConvertisseurDevise;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
        afficherPaneUnique(tableauBordPane);
    }
    
    @FXML
    public void afficherVoitures() {
        statusLabel.setText("Gestion des voitures");
        afficherPaneUnique(voituresPane);
    }
    
    @FXML
    public void afficherAssocies() {
        statusLabel.setText("Gestion des associÃ©s");
        afficherPaneUnique(associesPane);
    }
    
    @FXML
    public void afficherTauxChange() {
        statusLabel.setText("Gestion du taux de change");
        rafraichirTauxChange();
        afficherPaneUnique(tauxChangePane);
    }

    @FXML
    public void afficherBaseDonnees() {
        statusLabel.setText("Gestion base de donnees");
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
        try {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job == null) {
                statusLabel.setText("Impossible de creer le job d'impression");
                return;
            }

            boolean confirmer = job.showPrintDialog(mainContent.getScene().getWindow());
            if (!confirmer) {
                statusLabel.setText("Export annule");
                return;
            }

            // Tableau temporaire avec uniquement les colonnes demandees.
            TableView<Voiture> tableExport = new TableView<>();
            tableExport.setItems(FXCollections.observableArrayList(dashboardTable.getItems()));
            tableExport.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

            TableColumn<Voiture, String> cMarque = new TableColumn<>("Marque");
            cMarque.setPrefWidth(90);
            cMarque.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMarque()));

            TableColumn<Voiture, String> cModele = new TableColumn<>("Modele");
            cModele.setPrefWidth(90);
            cModele.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getModele()));

            TableColumn<Voiture, Integer> cAnnee = new TableColumn<>("Annee");
            cAnnee.setPrefWidth(65);
            cAnnee.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getAnnee()).asObject());

            TableColumn<Voiture, Double> cPrixAchat = new TableColumn<>("Prix Achat (CAD)");
            cPrixAchat.setPrefWidth(100);
            cPrixAchat.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrixAchatCAD()).asObject());
            setCurrencyCellFactory(cPrixAchat, "CAD");

            TableColumn<Voiture, Double> cTransport = new TableColumn<>("Transport (CAD)");
            cTransport.setPrefWidth(90);
            cTransport.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getTransportCAD()).asObject());
            setCurrencyCellFactory(cTransport, "CAD");

            TableColumn<Voiture, Double> cDedouanement = new TableColumn<>("Dedouanement (GNF)");
            cDedouanement.setPrefWidth(100);
            cDedouanement.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getDedouanementGNF()).asObject());
            setCurrencyCellFactory(cDedouanement, "GNF");

            TableColumn<Voiture, String> cDateImport = new TableColumn<>("Date Importation");
            cDateImport.setPrefWidth(100);
            cDateImport.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getDateImportation() == null ? "" : cell.getValue().getDateImportation().toString())
            );

            TableColumn<Voiture, Double> cPrixVente = new TableColumn<>("Prix Vente (GNF)");
            cPrixVente.setPrefWidth(100);
            cPrixVente.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrixReventeGNF()).asObject());
            cPrixVente.setCellFactory(c -> new TableCell<Voiture, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || getTableRow() == null || getTableRow().getItem() == null) {
                        setText(null);
                        return;
                    }
                    Voiture v = getTableRow().getItem();
                    if (estStatutVendu(v.getStatut())) {
                        setText("GNF " + String.format(Locale.US, "%,.0f", item));
                    } else {
                        setText("");
                    }
                }
            });

            TableColumn<Voiture, Double> cCoutTotal = new TableColumn<>("Cout Total (GNF)");
            cCoutTotal.setPrefWidth(110);
            cCoutTotal.setCellValueFactory(cell ->
                new SimpleDoubleProperty(VoitureController.calculerCoutTotal(cell.getValue())).asObject()
            );
            setCurrencyCellFactory(cCoutTotal, "GNF");

            tableExport.getColumns().addAll(
                cMarque, cModele, cAnnee, cPrixAchat, cTransport, cDedouanement, cDateImport, cPrixVente, cCoutTotal
            );

            double largeurColonnes = 0.0;
            for (TableColumn<Voiture, ?> col : tableExport.getColumns()) {
                largeurColonnes += col.getPrefWidth();
            }

            int nbLignes = tableExport.getItems() == null ? 0 : tableExport.getItems().size();
            double hauteurHeader = 32.0;
            double fixedCellSize = 28.0;
            tableExport.setFixedCellSize(fixedCellSize);
            double hauteurTotale = hauteurHeader + (nbLignes * fixedCellSize) + 2.0;
            tableExport.setPrefWidth(largeurColonnes + 2.0);
            tableExport.setMinWidth(largeurColonnes + 2.0);
            tableExport.setMaxWidth(largeurColonnes + 2.0);
            tableExport.setPrefHeight(hauteurTotale);
            tableExport.setMinHeight(hauteurTotale);
            tableExport.setMaxHeight(hauteurTotale);

            double totalAchatCad = 0.0;
            double totalDedouanementGnf = 0.0;
            double totalVenteGnf = 0.0;
            for (Voiture v : tableExport.getItems()) {
                totalAchatCad += v.getPrixAchatCAD();
                totalDedouanementGnf += v.getDedouanementGNF();
                if (estStatutVendu(v.getStatut())) {
                    totalVenteGnf += v.getPrixReventeGNF();
                }
            }

            Label titre = new Label("TABLEAU DES VOITURES");
            titre.setStyle("-fx-font-size: 21px; -fx-font-weight: 800;");
            Label sousTitre = new Label("Gestion Importation Voitures - Guinee");
            sousTitre.setStyle("-fx-font-size: 13px; -fx-font-weight: 600;");
            String dateExport = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            String numeroRapport = LocalDateTime.now().format(DateTimeFormatter.ofPattern("'RPT-'yyyyMMdd'-'HHmmss"));
            Label rapport = new Label("Rapport: " + numeroRapport);
            rapport.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #111827;");
            Label meta = new Label(
                "Date export: " + dateExport
                + " | Taux: 1 CAD = " + String.format(Locale.US, "%,.2f", ConvertisseurDevise.getTauxChange()) + " GNF"
                + " | Lignes: " + nbLignes
            );
            meta.setStyle("-fx-font-size: 11px; -fx-text-fill: #374151;");
            Label resume = new Label(
                "Total achat: CAD " + String.format(Locale.US, "%,.0f", totalAchatCad)
                + " | Total dedouanement: GNF " + String.format(Locale.US, "%,.0f", totalDedouanementGnf)
                + " | Total vente (vendues): GNF " + String.format(Locale.US, "%,.0f", totalVenteGnf)
            );
            resume.setStyle("-fx-font-size: 11px; -fx-text-fill: #374151;");

            ImageView logoGauche = creerLogoView();
            ImageView logoDroite = creerLogoView();

            VBox blocTexte = new VBox(3, titre, sousTitre, rapport, meta, resume);
            blocTexte.setAlignment(Pos.CENTER);

            Region espaceGauche = new Region();
            Region espaceDroite = new Region();
            HBox.setHgrow(espaceGauche, Priority.ALWAYS);
            HBox.setHgrow(espaceDroite, Priority.ALWAYS);

            HBox entete = new HBox(16, logoGauche, espaceGauche, blocTexte, espaceDroite, logoDroite);
            entete.setAlignment(Pos.CENTER);
            entete.setStyle("-fx-padding: 8 4 8 4;");

            Region separateur = new Region();
            separateur.setPrefHeight(2);
            separateur.setMinHeight(2);
            separateur.setMaxHeight(2);
            separateur.setPrefWidth(largeurColonnes + 2.0);
            separateur.setStyle("-fx-background-color: #1f2937;");

            Region separateurFooter = new Region();
            separateurFooter.setPrefHeight(1);
            separateurFooter.setMinHeight(1);
            separateurFooter.setMaxHeight(1);
            separateurFooter.setPrefWidth(largeurColonnes + 2.0);
            separateurFooter.setStyle("-fx-background-color: #d1d5db;");

            Label footerGauche = new Label("Genere par Import Voitures Guinee");
            footerGauche.setStyle("-fx-font-size: 10px; -fx-text-fill: #4b5563;");
            Label footerCentre = new Label("Confidentiel - Usage interne");
            footerCentre.setStyle("-fx-font-size: 10px; -fx-text-fill: #4b5563;");
            Label footerDroite = new Label("Page 1/1");
            footerDroite.setStyle("-fx-font-size: 10px; -fx-text-fill: #4b5563;");
            Region footerSpace1 = new Region();
            Region footerSpace2 = new Region();
            HBox.setHgrow(footerSpace1, Priority.ALWAYS);
            HBox.setHgrow(footerSpace2, Priority.ALWAYS);
            HBox footer = new HBox(8, footerGauche, footerSpace1, footerCentre, footerSpace2, footerDroite);
            footer.setAlignment(Pos.CENTER);
            footer.setPrefWidth(largeurColonnes + 2.0);

            VBox contentToPrint = new VBox(10, entete, separateur, tableExport, separateurFooter, footer);
            contentToPrint.setAlignment(Pos.TOP_CENTER);
            contentToPrint.setPrefWidth(largeurColonnes + 2.0);

            // Important: rattacher le noeud a une scene pour forcer la creation du skin avant impression.
            Group printRoot = new Group(contentToPrint);
            Scene printScene = new Scene(printRoot);
            printScene.getStylesheets().addAll(mainContent.getScene().getStylesheets());

            contentToPrint.applyCss();
            contentToPrint.layout();
            printRoot.applyCss();
            printRoot.layout();

            double largeurZone = contentToPrint.getBoundsInLocal().getWidth();
            double hauteurZone = contentToPrint.getBoundsInLocal().getHeight();

            // Force toujours le portrait.
            PageLayout portrait = job.getPrinter().createPageLayout(
                Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT
            );
            job.getJobSettings().setPageLayout(portrait);

            double largeurImprimable = portrait.getPrintableWidth();
            double hauteurImprimable = portrait.getPrintableHeight();

            double scaleX = largeurImprimable / largeurZone;
            double scaleY = hauteurImprimable / hauteurZone;
            double facteur = Math.min(scaleX, scaleY);

            Scale scale = new Scale(facteur, facteur);
            printRoot.getTransforms().add(scale);

            boolean imprime = job.printPage(printRoot);

            printRoot.getTransforms().remove(scale);

            if (imprime) {
                job.endJob();
                statusLabel.setText("Export PDF du tableau (colonnes filtrees) termine");
            } else {
                statusLabel.setText("Echec export PDF");
            }
        } catch (Exception e) {
            statusLabel.setText("Erreur export PDF: " + e.getMessage());
        }
    }

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
