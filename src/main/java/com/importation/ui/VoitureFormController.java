package com.importation.ui;

import com.importation.models.Associe;
import com.importation.models.Voiture;
import com.importation.models.dao.AssocieDAO;
import com.importation.models.dao.controllers.VoitureController;
import com.importation.models.dao.controllers.utils.ConvertisseurDevise;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Controleur du formulaire popup d'ajout de voiture.
 */
public class VoitureFormController {

    @FXML
    private TextField marqueField;
    @FXML
    private TextField modeleField;
    @FXML
    private TextField anneeSpinner;
    @FXML
    private TextField immatriculationField;
    @FXML
    private TextField prixAchatField;
    @FXML
    private TextField transportField;
    @FXML
    private TextField assuranceField;
    @FXML
    private TextField dedouanementField;
    @FXML
    private TextField fraisDiversCADField;
    @FXML
    private TextField fraisDiversGNFField;
    @FXML
    private TextField tauxChangeField;
    @FXML
    private TextField prixReventeField;
    @FXML
    private Label prixReventeLabel;
    @FXML
    private ComboBox<String> statutCombo;
    @FXML
    private DatePicker dateImportPicker;
    @FXML
    private ComboBox<Associe> associeCombo;

    private Runnable onSaved;
    private Voiture voitureEnEdition;

    @FXML
    public void initialize() {
        statutCombo.setItems(FXCollections.observableArrayList("EN_COURS", "VENDUE", "SUSPENDUE"));
        statutCombo.setValue("EN_COURS");
        statutCombo.valueProperty().addListener((obs, oldVal, newVal) -> mettreAJourVisibilitePrixRevente(newVal));
        dateImportPicker.setValue(LocalDate.now());
        tauxChangeField.setText(String.valueOf(ConvertisseurDevise.getTauxChange()));
        mettreAJourEditionTaux(false);

        try {
            associeCombo.setItems(FXCollections.observableArrayList(AssocieDAO.obtenirTous()));
        } catch (SQLException e) {
            afficherErreur("Erreur chargement", "Impossible de charger les associes: " + e.getMessage());
        }

        mettreAJourVisibilitePrixRevente(statutCombo.getValue());
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    public void setVoiture(Voiture voiture) {
        this.voitureEnEdition = voiture;
        if (voiture == null) {
            mettreAJourEditionTaux(false);
            return;
        }

        marqueField.setText(voiture.getMarque());
        modeleField.setText(voiture.getModele());
        anneeSpinner.setText(String.valueOf(voiture.getAnnee()));
        immatriculationField.setText(voiture.getImmatriculation() == null ? "" : voiture.getImmatriculation());
        prixAchatField.setText(String.valueOf(voiture.getPrixAchatCAD()));
        transportField.setText(String.valueOf(voiture.getTransportCAD()));
        assuranceField.setText(String.valueOf(voiture.getAssuranceCAD()));
        dedouanementField.setText(String.valueOf(voiture.getDedouanementGNF()));
        fraisDiversCADField.setText(String.valueOf(voiture.getFraisDiversCAD()));
        fraisDiversGNFField.setText(String.valueOf(voiture.getFraisDiversGNF()));
        tauxChangeField.setText(String.valueOf(obtenirTauxPourAffichage(voiture)));
        prixReventeField.setText(String.valueOf(voiture.getPrixReventeGNF()));
        dateImportPicker.setValue(voiture.getDateImportation());
        statutCombo.setValue(voiture.getStatut());
        mettreAJourVisibilitePrixRevente(voiture.getStatut());
        mettreAJourEditionTaux(true);

        if (voiture.getAssocieId() > 0) {
            for (Associe associe : associeCombo.getItems()) {
                if (associe.getId() == voiture.getAssocieId()) {
                    associeCombo.setValue(associe);
                    break;
                }
            }
        }
    }

    @FXML
    public void enregistrerVoiture() {
        try {
            if (marqueField.getText().trim().isEmpty() || modeleField.getText().trim().isEmpty()) {
                afficherErreur("Validation", "Marque et modele sont obligatoires");
                return;
            }

            Voiture voiture = new Voiture();
            voiture.setMarque(marqueField.getText().trim());
            voiture.setModele(modeleField.getText().trim());
            voiture.setAnnee(Integer.parseInt(anneeSpinner.getText().isEmpty() ? "0" : anneeSpinner.getText()));
            voiture.setImmatriculation(lireTexteNullable(immatriculationField));
            voiture.setPrixAchatCAD(lireDouble(prixAchatField));
            voiture.setTransportCAD(lireDouble(transportField));
            voiture.setAssuranceCAD(lireDouble(assuranceField));
            voiture.setDedouanementGNF(lireDouble(dedouanementField));
            voiture.setFraisDiversCAD(lireDouble(fraisDiversCADField));
            voiture.setFraisDiversGNF(lireDouble(fraisDiversGNFField));
            voiture.setTauxChangeCADGNF(lireTauxChange());
            voiture.setDateImportation(dateImportPicker.getValue() != null ? dateImportPicker.getValue() : LocalDate.now());
            voiture.setStatut(statutCombo.getValue() != null ? statutCombo.getValue() : "EN_COURS");

            if (estStatutVendu(voiture.getStatut())) {
                voiture.setPrixReventeGNF(lireDouble(prixReventeField));
            } else {
                voiture.setPrixReventeGNF(0.0);
            }

            voiture.setAssocieId(associeCombo.getValue() != null ? associeCombo.getValue().getId() : 0);

            if (!VoitureController.valider(voiture)) {
                afficherErreur(
                    "Validation",
                    "Verifiez les donnees: annee valide, prix d'achat > 0, date requise et prix revente > 0 si statut VENDUE."
                );
                return;
            }

            if (voitureEnEdition != null) {
                voiture.setId(voitureEnEdition.getId());
                VoitureController.mettre_a_jour(voiture);
            } else {
                VoitureController.ajouter(voiture);
            }

            if (onSaved != null) {
                onSaved.run();
            }

            fermer();
        } catch (NumberFormatException e) {
            afficherErreur("Erreur", "Les champs numeriques doivent contenir des nombres valides et le taux doit etre positif");
        } catch (SQLException e) {
            afficherErreur("Erreur BD", "Impossible d'enregistrer: " + e.getMessage());
        }
    }

    @FXML
    public void annuler() {
        fermer();
    }

    private double lireDouble(TextField field) {
        String text = field.getText().trim().replace(',', '.');
        return text.isEmpty() ? 0.0 : Double.parseDouble(text);
    }

    private double lireTauxChange() {
        double taux = lireDouble(tauxChangeField);
        if (taux <= 0) {
            throw new NumberFormatException("Le taux de change doit etre positif");
        }
        return taux;
    }

    private String lireTexteNullable(TextField field) {
        String text = field.getText();
        if (text == null) {
            return null;
        }
        String trimmed = text.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void mettreAJourVisibilitePrixRevente(String statut) {
        boolean vendu = estStatutVendu(statut);
        prixReventeLabel.setVisible(vendu);
        prixReventeLabel.setManaged(vendu);
        prixReventeField.setVisible(vendu);
        prixReventeField.setManaged(vendu);
        if (!vendu) {
            prixReventeField.clear();
        }
    }

    private boolean estStatutVendu(String statut) {
        if (statut == null) {
            return false;
        }
        String s = statut.trim().toUpperCase();
        return "VENDUE".equals(s) || "VENDU".equals(s);
    }

    private double obtenirTauxPourAffichage(Voiture voiture) {
        double taux = voiture.getTauxChangeCADGNF();
        return taux > 0 ? taux : ConvertisseurDevise.getTauxChange();
    }

    private void mettreAJourEditionTaux(boolean modeEdition) {
        tauxChangeField.setEditable(!modeEdition);
        tauxChangeField.setFocusTraversable(!modeEdition);
        if (modeEdition) {
            tauxChangeField.setPromptText("Taux historique conserve");
        } else {
            tauxChangeField.setPromptText("Ex: 6400");
        }
    }

    private void fermer() {
        Stage stage = (Stage) marqueField.getScene().getWindow();
        stage.close();
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
