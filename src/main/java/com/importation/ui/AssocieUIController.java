package com.importation.ui;

import com.importation.models.Associe;
import com.importation.models.Voiture;
import com.importation.models.dao.AssocieDAO;
import com.importation.models.dao.VoitureDAO;
import com.importation.models.dao.controllers.AssocieController;
import com.importation.models.dao.controllers.VoitureController;
import com.importation.models.dao.controllers.utils.Constantes;
import com.importation.models.dao.controllers.utils.ConvertisseurDevise;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

/**
 * Controleur pour l'interface des associes.
 */
public class AssocieUIController {

    @FXML
    private TableView<Associe> tableAssocies;

    @FXML
    private VBox formulaireVBox;

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField telephoneField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField pourcentageField;
    @FXML
    private ComboBox<Voiture> voitureContributionCombo;
    @FXML
    private TextField contributionField;
    @FXML
    private ComboBox<String> deviseContributionCombo;
    @FXML
    private TextField searchField;

    private Associe associeEnEdition;

    @FXML
    public void initialize() {
        configurerTableau();
        chargerAssocies();
        initialiserFormulaireContribution();
        initialiserCalculParticipationAuto();
    }

    private void initialiserFormulaireContribution() {
        deviseContributionCombo.setItems(FXCollections.observableArrayList(Constantes.DEVISE_GNF, Constantes.DEVISE_CAD));
        deviseContributionCombo.setValue(Constantes.DEVISE_GNF);
        pourcentageField.setEditable(false);
        pourcentageField.setFocusTraversable(false);
        try {
            voitureContributionCombo.setItems(FXCollections.observableArrayList(VoitureDAO.obtenirTous()));
        } catch (SQLException e) {
            afficherErreur("Erreur chargement", "Impossible de charger les voitures: " + e.getMessage());
        }
    }

    private void initialiserCalculParticipationAuto() {
        contributionField.textProperty().addListener((obs, oldVal, newVal) -> recalculerParticipation());
        deviseContributionCombo.valueProperty().addListener((obs, oldVal, newVal) -> recalculerParticipation());
        voitureContributionCombo.valueProperty().addListener((obs, oldVal, newVal) -> recalculerParticipation());
    }

    private void recalculerParticipation() {
        String contributionText = contributionField.getText() != null ? contributionField.getText().trim() : "";
        if (contributionText.isEmpty() || voitureContributionCombo.getValue() == null) {
            if (associeEnEdition != null) {
                pourcentageField.setText(String.format(Locale.US, "%.2f", associeEnEdition.getPourcentageParticipation()));
            } else {
                pourcentageField.setText("0.00");
            }
            return;
        }

        try {
            double montant = Double.parseDouble(contributionText);
            if (montant <= 0) {
                pourcentageField.setText("0.00");
                return;
            }

            Voiture voiture = voitureContributionCombo.getValue();
            double coutTotalVoitureGNF = VoitureController.calculerCoutTotal(voiture);
            if (coutTotalVoitureGNF <= 0) {
                pourcentageField.setText("0.00");
                return;
            }

            String devise = deviseContributionCombo.getValue() != null ? deviseContributionCombo.getValue() : Constantes.DEVISE_GNF;
            double contributionGNF = Constantes.DEVISE_CAD.equals(devise)
                ? ConvertisseurDevise.cadVersGnf(montant)
                : montant;

            double pourcentage = (contributionGNF / coutTotalVoitureGNF) * 100.0;
            pourcentageField.setText(String.format(Locale.US, "%.2f", pourcentage));
        } catch (NumberFormatException e) {
            pourcentageField.setText("0.00");
        }
    }

    private void configurerTableau() {
        if (tableAssocies.getColumns().size() >= 6) {
            ((TableColumn<Associe, Integer>) tableAssocies.getColumns().get(0)).setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject()
            );
            ((TableColumn<Associe, String>) tableAssocies.getColumns().get(1)).setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom())
            );
            ((TableColumn<Associe, String>) tableAssocies.getColumns().get(2)).setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPrenom())
            );
            ((TableColumn<Associe, String>) tableAssocies.getColumns().get(3)).setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTelephone())
            );
            ((TableColumn<Associe, String>) tableAssocies.getColumns().get(4)).setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail())
            );
            ((TableColumn<Associe, Double>) tableAssocies.getColumns().get(5)).setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPourcentageParticipation()).asObject()
            );
        }
    }

    private void chargerAssocies() {
        try {
            List<Associe> associes = AssocieDAO.obtenirTous();
            ObservableList<Associe> data = FXCollections.observableArrayList(associes);
            tableAssocies.setItems(data);
        } catch (SQLException e) {
            afficherErreur("Erreur chargement", "Impossible de charger les associes: " + e.getMessage());
        }
    }

    @FXML
    public void afficherFormulaire() {
        associeEnEdition = null;
        formulaireVBox.setVisible(true);
        formulaireVBox.setManaged(true);
        effacerFormulaire();
        initialiserFormulaireContribution();
    }

    @FXML
    public void modifierAssocie() {
        Associe selection = tableAssocies.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherErreur("Selection", "Veuillez selectionner un associe a modifier");
            return;
        }

        associeEnEdition = selection;
        formulaireVBox.setVisible(true);
        formulaireVBox.setManaged(true);

        nomField.setText(selection.getNom());
        prenomField.setText(selection.getPrenom());
        telephoneField.setText(selection.getTelephone() == null ? "" : selection.getTelephone());
        emailField.setText(selection.getEmail() == null ? "" : selection.getEmail());
        pourcentageField.setText(String.format(Locale.US, "%.2f", selection.getPourcentageParticipation()));
        contributionField.clear();
        voitureContributionCombo.setValue(null);
        deviseContributionCombo.setValue(Constantes.DEVISE_GNF);
    }

    @FXML
    public void supprimerAssocie() {
        Associe selection = tableAssocies.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherErreur("Selection", "Veuillez selectionner un associe a supprimer");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'associe selectionne ?");
        confirm.setContentText(selection.getPrenom() + " " + selection.getNom());
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            AssocieController.supprimer(selection.getId());
            chargerAssocies();
        } catch (SQLException e) {
            afficherErreur("Erreur suppression", "Impossible de supprimer: " + e.getMessage());
        }
    }

    @FXML
    public void masquerFormulaire() {
        formulaireVBox.setVisible(false);
        formulaireVBox.setManaged(false);
        associeEnEdition = null;
        effacerFormulaire();
    }

    @FXML
    public void enregistrerAssocie() {
        try {
            if (nomField.getText().trim().isEmpty() || prenomField.getText().trim().isEmpty()) {
                afficherErreur("Validation", "Nom et prenom sont obligatoires");
                return;
            }

            String contributionText = contributionField.getText().trim();
            double montantContribution = 0.0;
            if (!contributionText.isEmpty()) {
                if (voitureContributionCombo.getValue() == null) {
                    afficherErreur("Validation", "Selectionnez une voiture pour la contribution");
                    return;
                }
                montantContribution = Double.parseDouble(contributionText);
                if (montantContribution <= 0) {
                    afficherErreur("Validation", "Le montant de contribution doit etre superieur a 0");
                    return;
                }
            }

            Associe associe = associeEnEdition != null ? associeEnEdition : new Associe();
            associe.setNom(nomField.getText().trim());
            associe.setPrenom(prenomField.getText().trim());
            associe.setTelephone(telephoneField.getText().trim());
            associe.setEmail(emailField.getText().trim());

            recalculerParticipation();
            String pourcentageText = pourcentageField.getText().trim();
            double pourcentage = pourcentageText.isEmpty() ? 0.0 : Double.parseDouble(pourcentageText);
            if (associeEnEdition != null && contributionText.isEmpty()) {
                pourcentage = associeEnEdition.getPourcentageParticipation();
            }
            associe.setPourcentageParticipation(pourcentage);

            int associeId;
            if (associeEnEdition != null) {
                AssocieController.mettre_a_jour(associe);
                associeId = associe.getId();
            } else {
                associeId = AssocieController.ajouter(associe);
            }

            if (!contributionText.isEmpty()) {
                AssocieController.enregistrerContribution(
                    associeId,
                    voitureContributionCombo.getValue().getId(),
                    montantContribution,
                    deviseContributionCombo.getValue() != null ? deviseContributionCombo.getValue() : Constantes.DEVISE_GNF,
                    "Contribution associe " + associe.getPrenom() + " " + associe.getNom()
                );
            }

            afficherInfo("Succes", "Associe enregistre avec succes");
            masquerFormulaire();
            chargerAssocies();

        } catch (NumberFormatException e) {
            afficherErreur("Erreur", "Le pourcentage et la contribution doivent etre des nombres");
        } catch (SQLException e) {
            afficherErreur("Erreur BD", "Impossible d'enregistrer: " + e.getMessage());
        }
    }

    @FXML
    public void rechercher() {
        String search = searchField.getText().toLowerCase().trim();
        if (search.isEmpty()) {
            chargerAssocies();
            return;
        }

        try {
            List<Associe> associes = AssocieDAO.obtenirTous();
            ObservableList<Associe> filtered = FXCollections.observableArrayList();

            for (Associe a : associes) {
                if ((a.getNom() != null && a.getNom().toLowerCase().contains(search))
                    || (a.getPrenom() != null && a.getPrenom().toLowerCase().contains(search))
                    || (a.getTelephone() != null && a.getTelephone().toLowerCase().contains(search))) {
                    filtered.add(a);
                }
            }

            tableAssocies.setItems(filtered);
        } catch (SQLException e) {
            afficherErreur("Erreur", "Erreur de recherche: " + e.getMessage());
        }
    }

    @FXML
    public void afficherTous() {
        searchField.clear();
        chargerAssocies();
    }

    private void effacerFormulaire() {
        nomField.clear();
        prenomField.clear();
        telephoneField.clear();
        emailField.clear();
        pourcentageField.setText("0.00");
        contributionField.clear();
        voitureContributionCombo.setValue(null);
        deviseContributionCombo.setValue(Constantes.DEVISE_GNF);
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherInfo(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
