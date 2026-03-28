package com.importation.models.dao.controllers;

import com.importation.models.Voiture;
import com.importation.models.dao.VoitureDAO;
import com.importation.models.dao.controllers.utils.ConvertisseurDevise;
import java.sql.SQLException;
import java.util.List;


public class VoitureController {
    
    /**
     * Ajoute une nouvelle voiture
     */
    public static int ajouter(Voiture voiture) throws SQLException {
        return VoitureDAO.ajouter(voiture);
    }
    
   
    public static void mettre_a_jour(Voiture voiture) throws SQLException {
        VoitureDAO.mettre_a_jour(voiture);
    }
    
    /**
     * Supprime une voiture
     */
    public static void supprimer(int id) throws SQLException {
        VoitureDAO.supprimer(id);
    }
    
    
    public static Voiture obtenirParId(int id) throws SQLException {
        return VoitureDAO.obtenirParId(id);
    }
    
   
    public static List<Voiture> obtenirTous() throws SQLException {
        return VoitureDAO.obtenirTous();
    }
    
    
    public static List<Voiture> obtenirParStatut(String statut) throws SQLException {
        return VoitureDAO.obtenirParStatut(statut);
    }
    
   
    public static List<Voiture> obtenirParAssocie(int associeId) throws SQLException {
        return VoitureDAO.obtenirParAssocie(associeId);
    }
    
   
    public static double calculerCoutTotal(Voiture voiture) {
        double fraisCAD = voiture.getPrixAchatCAD() + 
                         voiture.getTransportCAD() + 
                         voiture.getAssuranceCAD() + 
                         voiture.getFraisDiversCAD();
        
        double fraisCADEnGNF = ConvertisseurDevise.cadVersGnf(fraisCAD);
        
        return fraisCADEnGNF + voiture.getDedouanementGNF() + voiture.getFraisDiversGNF();
    }
    
    
    public static double calculerBeneficeNet(Voiture voiture) {
        return voiture.getPrixReventeGNF() - calculerCoutTotal(voiture);
    }
    
    
    public static double calculerMargeBeneficiaire(Voiture voiture) {
        double coutTotal = calculerCoutTotal(voiture);
        if (coutTotal == 0) {
            return 0;
        }
        return (calculerBeneficeNet(voiture) / coutTotal) * 100;
    }
    
    
    public static double calculerPrixAchatTotalCAD(Voiture voiture) {
        return voiture.getPrixAchatCAD() + 
               voiture.getTransportCAD() + 
               voiture.getAssuranceCAD() + 
               voiture.getFraisDiversCAD();
    }
    
    
    public static double calculerFraisGuineeTotaux(Voiture voiture) {
        return voiture.getDedouanementGNF() + voiture.getFraisDiversGNF();
    }
    
   
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

