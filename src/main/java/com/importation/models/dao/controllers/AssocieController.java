package com.importation.models.dao.controllers;

import com.importation.models.Associe;
import com.importation.models.Paiement;
import com.importation.models.dao.AssocieDAO;
import com.importation.models.dao.PaimentDAO;
import com.importation.models.dao.controllers.utils.ConvertisseurDevise;
import com.importation.models.dao.controllers.utils.Constantes;
import java.sql.SQLException;
import java.util.List;

/**
 * ContrÃƒÆ’Ã‚Â´leur pour la gestion des associÃƒÆ’Ã‚Â©s
 * GÃƒÆ’Ã‚Â¨re les contributions et la rÃƒÆ’Ã‚Â©partition des bÃƒÆ’Ã‚Â©nÃƒÆ’Ã‚Â©fices
 */
public class AssocieController {
    
    /**
     * Ajoute un nouvel associÃƒÆ’Ã‚Â©
     */
    public static int ajouter(Associe associe) throws SQLException {
        return AssocieDAO.ajouter(associe);
    }

    /**
     * Enregistre une contribution financiere d'un associe sur une voiture.
     */
    public static int enregistrerContribution(int associeId, int voitureId, double montant, String devise, String description) throws SQLException {
        Paiement paiement = new Paiement();
        paiement.setAssocieId(associeId);
        paiement.setVoitureId(voitureId);
        paiement.setMontant(montant);
        paiement.setDevise(devise);
        paiement.setDatePaiement(java.time.LocalDate.now());
        paiement.setTypePaiement(Constantes.TYPE_PAIEMENT_CONTRIBUTION);
        paiement.setDescription(description != null ? description : "Contribution associe #" + associeId);
        return PaimentDAO.ajouter(paiement);
    }
    
    /**
     * Met ÃƒÆ’Ã‚Â  jour un associÃƒÆ’Ã‚Â©
     */
    public static void mettre_a_jour(Associe associe) throws SQLException {
        AssocieDAO.mettre_a_jour(associe);
    }
        /**
     * Supprime un associÃƒÆ’Ã‚Â©
     */
    public static void supprimer(int id) throws SQLException {
        AssocieDAO.supprimer(id);
    }
    
    /**
     * RÃƒÆ’Ã‚Â©cupÃƒÆ’Ã‚Â¨re un associÃƒÆ’Ã‚Â©
     */
    public static Associe obtenirParId(int id) throws SQLException {
        return AssocieDAO.obtenirParId(id);
    }
    
    /**
     * RÃƒÆ’Ã‚Â©cupÃƒÆ’Ã‚Â¨re tous les associÃƒÆ’Ã‚Â©s
     */
    public static List<Associe> obtenirTous() throws SQLException {
        return AssocieDAO.obtenirTous();
    }
    
    /**
     * Calcule le total des contributions d'un associÃƒÆ’Ã‚Â© en GNF
     */
    public static double calculerTotalContributions(int associeId) throws SQLException {
        List<Paiement> paiements = PaimentDAO.obtenirParAssocie(associeId);
        double totalGNF = 0;
        
        for (Paiement paiement : paiements) {
            if (paiement.getTypePaiement().equals(Constantes.TYPE_PAIEMENT_CONTRIBUTION)) {
                if (paiement.getDevise().equals(Constantes.DEVISE_CAD)) {
                    totalGNF += ConvertisseurDevise.cadVersGnf(paiement.getMontant());
                } else {
                    totalGNF += paiement.getMontant();
                }
            }
        }
        
        return totalGNF;
    }
    
    /**
     * Calcule le total des revenus d'un associÃƒÆ’Ã‚Â© en GNF
     */
    public static double calculerTotalRevenus(int associeId) throws SQLException {
        List<Paiement> paiements = PaimentDAO.obtenirParAssocie(associeId);
        double totalGNF = 0;
        
        for (Paiement paiement : paiements) {
            if (paiement.getTypePaiement().equals(Constantes.TYPE_PAIEMENT_REVENU)) {
                if (paiement.getDevise().equals(Constantes.DEVISE_CAD)) {
                    totalGNF += ConvertisseurDevise.cadVersGnf(paiement.getMontant());
                } else {
                    totalGNF += paiement.getMontant();
                }
            }
        }
        
        return totalGNF;
    }
    
    /**
     * Calcule le solde net d'un associÃƒÆ’Ã‚Â© (revenus - contributions) en GNF
     */
    public static double calculerSoldeNet(int associeId) throws SQLException {
        return calculerTotalRevenus(associeId) - calculerTotalContributions(associeId);
    }
    
    /**
     * RÃƒÆ’Ã‚Â©partit un bÃƒÆ’Ã‚Â©nÃƒÆ’Ã‚Â©fice entre les associÃƒÆ’Ã‚Â©s selon leurs pourcentages
     */
    public static void repartirBenefice(int voitureId, double montantBeneficeGNF, String description) throws SQLException {
        List<Associe> associes = obtenirTous();
        
        for (Associe associe : associes) {
            double partBenefice = (montantBeneficeGNF * associe.getPourcentageParticipation()) / 100;
            
            if (partBenefice > 0) {
                Paiement paiement = new Paiement();
                paiement.setVoitureId(voitureId);
                paiement.setAssocieId(associe.getId());
                paiement.setMontant(partBenefice);
                paiement.setDevise(Constantes.DEVISE_GNF);
                paiement.setDatePaiement(java.time.LocalDate.now());
                paiement.setTypePaiement(Constantes.TYPE_PAIEMENT_REVENU);
                paiement.setDescription(description != null ? description : "RÃƒÆ’Ã‚Â©partition bÃƒÆ’Ã‚Â©nÃƒÆ’Ã‚Â©fice voiture #" + voitureId);
                
                PaimentDAO.ajouter(paiement);
            }
        }
    }
    
    /**
     * Valide les donnÃƒÆ’Ã‚Â©es d'un associÃƒÆ’Ã‚Â©
     */
    public static boolean valider(Associe associe) {
        if (associe.getNom() == null || associe.getNom().trim().isEmpty()) {
            return false;
        }
        if (associe.getPrenom() == null || associe.getPrenom().trim().isEmpty()) {
            return false;
        }
        if (associe.getPourcentageParticipation() < 0 || associe.getPourcentageParticipation() > 100) {
            return false;
        }
        return true;
    }
}

