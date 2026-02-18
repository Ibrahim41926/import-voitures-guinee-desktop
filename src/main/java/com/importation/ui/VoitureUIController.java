package com.importation.ui;

import com.importation.models.Voiture;
import com.importation.models.dao.VoitureDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Controleur de la vue voitures (liste + recherche).
 * Le formulaire d'ajout s'ouvre dans une fenetre modale.
 */
public class VoitureUIController {

    @FXML
    private TableView<Voiture> tableVoitures;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        configurerTableau();
        chargerVoitures();
    }

    private void configurerTableau() {
        if (tableVoitures.getColumns().size() >= 8) {
            ((TableColumn<Voiture, Integer>) tableVoitures.getColumns().get(0)).setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject()
            );
            ((TableColumn<Voiture, String>) tableVoitures.getColumns().get(1)).setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMarque())
            );
            ((TableColumn<Voiture, String>) tableVoitures.getColumns().get(2)).setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getModele())
            );
            ((TableColumn<Voiture, Integer>) tableVoitures.getColumns().get(3)).setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getAnnee()).asObject()
            );
            ((TableColumn<Voiture, String>) tableVoitures.getColumns().get(4)).setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getImmatriculation())
            );
            ((TableColumn<Voiture, Double>) tableVoitures.getColumns().get(5)).setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPrixAchatCAD()).asObject()
            );
            ((TableColumn<Voiture, String>) tableVoitures.getColumns().get(6)).setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatut())
            );
            ((TableColumn<Voiture, String>) tableVoitures.getColumns().get(7)).setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDateImportation() != null ? cellData.getValue().getDateImportation().toString() : ""
                )
            );
        }
    }

    @FXML
    public void afficherFormulaire() {
        ouvrirFormulaireVoiture(null);
    }

    @FXML
    public void modifierVoiture() {
        Voiture selection = tableVoitures.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherErreur("Selection", "Veuillez selectionner une voiture a modifier");
            return;
        }
        ouvrirFormulaireVoiture(selection);
    }

    @FXML
    public void supprimerVoiture() {
        Voiture selection = tableVoitures.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherErreur("Selection", "Veuillez selectionner une voiture a supprimer");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la voiture selectionnee ?");
        confirm.setContentText(selection.getMarque() + " " + selection.getModele() + " (" + selection.getImmatriculation() + ")");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            VoitureDAO.supprimer(selection.getId());
            chargerVoitures();
        } catch (SQLException e) {
            afficherErreur("Erreur suppression", "Impossible de supprimer: " + e.getMessage());
        }
    }

    private void ouvrirFormulaireVoiture(Voiture voiture) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/importation/models/resources/fxml/voiture_form.fxml")
            );
            Parent root = loader.load();

            VoitureFormController formController = loader.getController();
            formController.setOnSaved(this::chargerVoitures);
            if (voiture != null) {
                formController.setVoiture(voiture);
            }

            Stage stage = new Stage();
            stage.setTitle(voiture == null ? "Nouvelle voiture" : "Modifier voiture");
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            String css = getClass().getResource("/com/importation/models/resources/fxml/css/style.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            afficherErreur("Erreur chargement", "Impossible d'ouvrir le formulaire: " + e.getMessage());
        }
    }

    @FXML
    public void rechercher() {
        String search = searchField.getText().toLowerCase().trim();
        if (search.isEmpty()) {
            chargerVoitures();
            return;
        }

        try {
            List<Voiture> voitures = VoitureDAO.obtenirTous();
            ObservableList<Voiture> filtered = FXCollections.observableArrayList();

            for (Voiture v : voitures) {
                if ((v.getMarque() != null && v.getMarque().toLowerCase().contains(search))
                    || (v.getModele() != null && v.getModele().toLowerCase().contains(search))
                    || (v.getImmatriculation() != null && v.getImmatriculation().toLowerCase().contains(search))) {
                    filtered.add(v);
                }
            }

            tableVoitures.setItems(filtered);
        } catch (SQLException e) {
            afficherErreur("Erreur", "Erreur de recherche: " + e.getMessage());
        }
    }

    @FXML
    public void afficherTous() {
        searchField.clear();
        chargerVoitures();
    }

    private void chargerVoitures() {
        try {
            List<Voiture> voitures = VoitureDAO.obtenirTous();
            tableVoitures.setItems(FXCollections.observableArrayList(voitures));
        } catch (SQLException e) {
            afficherErreur("Erreur chargement", "Impossible de charger les voitures: " + e.getMessage());
        }
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
