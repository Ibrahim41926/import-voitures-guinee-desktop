package com.importation.models.dao.controllers;

import com.importation.models.Voiture;
import com.importation.models.dao.VoitureDAO;
import com.importation.models.dao.controllers.utils.ConvertisseurDevise;
import java.sql.SQLException;
import java.util.List;

/**
 * ContrÃƒÆ’Ã‚Â´leur pour la gestion des voitures
 * GÃƒÆ’Ã‚Â¨re la logique mÃƒÆ’Ã‚Â©tier et les calculs
 */
public class VoitureController {
    
    /**
     * Ajoute une nouvelle voiture
     */
    public static int ajouter(Voiture voiture) throws SQLException {
        return VoitureDAO.ajouter(voiture);
    }
    
    /**
     * Met ÃƒÆ’Ã‚Â  jour une voiture
     */
    public static void mettre_a_jour(Voiture voiture) throws SQLException {
        VoitureDAO.mettre_a_jour(voiture);
    }
    
    /**
     * Supprime une voiture
     */
    public static void supprimer(int id) throws SQLException {
        VoitureDAO.supprimer(id);
    }
    
    /**
     * RÃƒÆ’Ã‚Â©cupÃƒÆ’Ã‚Â¨re une voiture
     */
    public static Voiture obtenirParId(int id) throws SQLException {
        return VoitureDAO.obtenirParId(id);
    }
    
    /**
     * RÃƒÆ’Ã‚Â©cupÃƒÆ’Ã‚Â¨re toutes les voitures
     */
    public static List<Voiture> obtenirTous() throws SQLException {
        return VoitureDAO.obtenirTous();
    }
    
    /**
     * RÃƒÆ’Ã‚Â©cupÃƒÆ’Ã‚Â¨re les voitures par statut
     */
    public static List<Voiture> obtenirParStatut(String statut) throws SQLException {
        return VoitureDAO.obtenirParStatut(statut);
    }
    
    /**
     * RÃƒÆ’Ã‚Â©cupÃƒÆ’Ã‚Â¨re les voitures d'un associÃƒÆ’Ã‚Â©
     */
    public static List<Voiture> obtenirParAssocie(int associeId) throws SQLException {
        return VoitureDAO.obtenirParAssocie(associeId);
    }
    
    /**
     * Calcule le coÃƒÆ’Ã‚Â»t total d'importation en GNF
     * CoÃƒÆ’Ã‚Â»t total = (PrixAchat + Transport + Assurance + FraisDivers) converti en GNF + DÃƒÆ’Ã‚Â©douanement + FraisDiversGNF
     */
    public static double calculerCoutTotal(Voiture voiture) {
        double fraisCAD = voiture.getPrixAchatCAD() + 
                         voiture.getTransportCAD() + 
                         voiture.getAssuranceCAD() + 
                         voiture.getFraisDiversCAD();
        
        double fraisCADEnGNF = ConvertisseurDevise.cadVersGnf(fraisCAD);
        
        return fraisCADEnGNF + voiture.getDedouanementGNF() + voiture.getFraisDiversGNF();
    }
    
    /**
     * Calcule le bÃƒÆ’Ã‚Â©nÃƒÆ’Ã‚Â©fice net
     * BÃƒÆ’Ã‚Â©nÃƒÆ’Ã‚Â©fice = PrixRevente - CoutTotal
     */
    public static double calculerBeneficeNet(Voiture voiture) {
        return voiture.getPrixReventeGNF() - calculerCoutTotal(voiture);
    }
    
    /**
     * Calcule la marge bÃƒÆ’Ã‚Â©nÃƒÆ’Ã‚Â©ficiaire en pourcentage
     */
    public static double calculerMargeBeneficiaire(Voiture voiture) {
        double coutTotal = calculerCoutTotal(voiture);
        if (coutTotal == 0) {
            return 0;
        }
        return (calculerBeneficeNet(voiture) / coutTotal) * 100;
    }
    
    /**
     * Calcule le prix d'achat total en CAD
     */
    public static double calculerPrixAchatTotalCAD(Voiture voiture) {
        return voiture.getPrixAchatCAD() + 
               voiture.getTransportCAD() + 
               voiture.getAssuranceCAD() + 
               voiture.getFraisDiversCAD();
    }
    
    /**
     * Calcule les frais de GuinÃƒÆ’Ã‚Â©e totaux en GNF
     */
    public static double calculerFraisGuineeTotaux(Voiture voiture) {
        return voiture.getDedouanementGNF() + voiture.getFraisDiversGNF();
    }
    
    /**
     * Valide une voiture avant sauvegarde
     */
    public static boolean valider(Voiture voiture) {
        if (voiture.getMarque() == null || voiture.getMarque().trim().isEmpty()) {
            return false;
        }
        if (voiture.getModele() == null || voiture.getModele().trim().isEmpty()) {
            return false;
        }
        if (voiture.getAnnee() < 1900 || voiture.getAnnee() > 2030) {
            return false;
        }
        if (voiture.getPrixAchatCAD() <= 0) {
            return false;
        }
        if (voiture.getDateImportation() == null) {
            return false;
        }
        if (estStatutVendu(voiture.getStatut()) && voiture.getPrixReventeGNF() <= 0) {
            return false;
        }
        return true;
    }

    private static boolean estStatutVendu(String statut) {
        if (statut == null) {
            return false;
        }
        String s = statut.trim().toUpperCase();
        return "VENDUE".equals(s) || "VENDU".equals(s);
    }
}

