package com.importation.models.dao.controllers.utils;

import com.importation.models.dao.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe utilitaire pour convertir entre CAD et GNF.
 */
public class ConvertisseurDevise {
    private static final String CLE_TAUX_CHANGE = "TAUX_CHANGE";

    // Taux de change courant (1 CAD = X GNF).
    private static double tauxChange = Constantes.TAUX_CHANGE_DEFAUT;

    /**
     * Convertit de CAD vers GNF.
     */
    public static double cadVersGnf(double montantCAD) {
        return cadVersGnf(montantCAD, tauxChange);
    }

    /**
     * Convertit de CAD vers GNF avec un taux explicite.
     */
    public static double cadVersGnf(double montantCAD, double tauxChangeApplique) {
        validerMontantNonNegatif(montantCAD);
        validerTauxPositif(tauxChangeApplique);
        return montantCAD * tauxChangeApplique;
    }

    /**
     * Convertit de GNF vers CAD.
     */
    public static double gnfVersCad(double montantGNF) {
        validerMontantNonNegatif(montantGNF);
        return montantGNF / tauxChange;
    }

    /**
     * Obtient le taux de change courant.
     */
    public static double getTauxChange() {
        return tauxChange;
    }

    /**
     * Definit le taux en memoire.
     */
    public static void setTauxChange(double nouveauTaux) {
        validerTauxPositif(nouveauTaux);
        tauxChange = nouveauTaux;
    }

    /**
     * Charge le taux depuis la base puis l'applique.
     */
    public static void initialiserDepuisBase() throws SQLException {
        String sql = "SELECT valeur FROM CONFIGURATION WHERE cle = ?";
        try (PreparedStatement pstmt = DatabaseConnection.obtenirConnexion().prepareStatement(sql)) {
            pstmt.setString(1, CLE_TAUX_CHANGE);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double valeur = rs.getDouble("valeur");
                    if (valeur > 0) {
                        setTauxChange(valeur);
                        return;
                    }
                }
            }
        }

        // Fallback: appliquer puis persister le taux par defaut.
        setTauxChange(Constantes.TAUX_CHANGE_DEFAUT);
        sauvegarderTauxEnBase();
    }

    /**
     * Definit et sauvegarde un nouveau taux persistant.
     */
    public static void setTauxChangeEtSauvegarder(double nouveauTaux) throws SQLException {
        setTauxChange(nouveauTaux);
        sauvegarderTauxEnBase();
    }

    private static void sauvegarderTauxEnBase() throws SQLException {
        String sql = "INSERT INTO CONFIGURATION (cle, valeur) VALUES (?, ?) "
            + "ON CONFLICT(cle) DO UPDATE SET valeur=excluded.valeur";
        try (PreparedStatement pstmt = DatabaseConnection.obtenirConnexion().prepareStatement(sql)) {
            pstmt.setString(1, CLE_TAUX_CHANGE);
            pstmt.setDouble(2, tauxChange);
            pstmt.executeUpdate();
        }
    }

    /**
     * Convertit un montant d'une devise a une autre.
     */
    public static double convertir(double montant, String deviseSource, String deviseCible) {
        if (!deviseSource.equals(deviseCible)) {
            if (deviseSource.equals(Constantes.DEVISE_CAD) && deviseCible.equals(Constantes.DEVISE_GNF)) {
                return cadVersGnf(montant);
            } else if (deviseSource.equals(Constantes.DEVISE_GNF) && deviseCible.equals(Constantes.DEVISE_CAD)) {
                return gnfVersCad(montant);
            }
        }
        return montant;
    }

    /**
     * Formate un montant selon la devise.
     */
    public static String formater(double montant, String devise) {
        if (devise.equals(Constantes.DEVISE_CAD)) {
            return String.format("%.2f %s", montant, devise);
        } else if (devise.equals(Constantes.DEVISE_GNF)) {
            return String.format("%.0f %s", montant, devise);
        }
        return String.format("%.2f %s", montant, devise);
    }

    private static void validerMontantNonNegatif(double montant) {
        if (montant < 0) {
            throw new IllegalArgumentException("Le montant ne peut pas etre negatif");
        }
    }

    private static void validerTauxPositif(double taux) {
        if (taux <= 0) {
            throw new IllegalArgumentException("Le taux de change doit etre positif");
        }
    }
}
