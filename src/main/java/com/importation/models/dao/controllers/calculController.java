package com.importation.models.dao.controllers;

import com.importation.models.Voiture;
import com.importation.models.dao.VoitureDAO;
import com.importation.models.dao.controllers.utils.ConvertisseurDevise;
import java.sql.SQLException;
import java.util.List;

/**
 * Contrôleur pour les calculs de synthèse et bilan
 */
public class calculController {
    
    /**
     * Calcule le benéfice total de toutes les voitures vendues
     */
    public static double calculerBeneficeTotal() throws SQLException {
        List<Voiture> voitures = VoitureDAO.obtenirTous();
        double beneficeTotal = 0;
        
        for (Voiture voiture : voitures) {
            if ("VENDUE".equals(voiture.getStatut())) {
                beneficeTotal += VoitureController.calculerBeneficeNet(voiture);
            }
        }
        
        return beneficeTotal;
    }
    
    /**
     * Calcule le coût total d'importation
     */
    public static double calculerCoutTotalImportation() throws SQLException {
        List<Voiture> voitures = VoitureDAO.obtenirTous();
        double coutTotal = 0;
        
        for (Voiture voiture : voitures) {
            coutTotal += VoitureController.calculerCoutTotal(voiture);
        }
        
        return coutTotal;
    }
    
    /**
     * Calcule le revenu total (somme des prix de revente)
     */
    public static double calculerRevenuTotal() throws SQLException {
        List<Voiture> voitures = VoitureDAO.obtenirParStatut("VENDUE");
        double revenuTotal = 0;
        
        for (Voiture voiture : voitures) {
            revenuTotal += voiture.getPrixReventeGNF();
        }
        
        return revenuTotal;
    }
    
    /**
     * Calcule le nombre de voitures en cours
     */
    public static int compterVoituresEnCours() throws SQLException {
        return VoitureDAO.obtenirParStatut("EN_COURS").size();
    }
    
    /**
     * Calcule le nombre de voitures vendues
     */
    public static int compterVoituresVendues() throws SQLException {
        return VoitureDAO.obtenirParStatut("VENDUE").size();
    }
    
    /**
     * Calcule le nombre total de voitures
     */
    public static int compterVoituresTotales() throws SQLException {
        return VoitureDAO.obtenirTous().size();
    }
    
    /**
     * Génère un résumé financier
     */
    public static ResumFinancier genererResumFinancier() throws SQLException {
        ResumFinancier resum = new ResumFinancier();
        resum.setNombreVoituresTotales(compterVoituresTotales());
        resum.setNombreVoituresVendues(compterVoituresVendues());
        resum.setNombreVoituresEnCours(compterVoituresEnCours());
        resum.setCoutTotalImportation(calculerCoutTotalImportation());
        resum.setRevenuTotal(calculerRevenuTotal());
        resum.setBeneficeTotal(calculerBeneficeTotal());
        
        if (resum.getCoutTotalImportation() > 0) {
            resum.setMargeMoyenne((resum.getBeneficeTotal() / resum.getCoutTotalImportation()) * 100);
        }
        
        return resum;
    }
    
    /**
     * Classe interne pour le résumé financier
     */
    public static class ResumFinancier {
        private int nombreVoituresTotales;
        private int nombreVoituresVendues;
        private int nombreVoituresEnCours;
        private double coutTotalImportation;
        private double revenuTotal;
        private double beneficeTotal;
        private double margeMoyenne;
        
        // Getters et Setters
        public int getNombreVoituresTotales() {
            return nombreVoituresTotales;
        }
        
        public void setNombreVoituresTotales(int nombreVoituresTotales) {
            this.nombreVoituresTotales = nombreVoituresTotales;
        }
        
        public int getNombreVoituresVendues() {
            return nombreVoituresVendues;
        }
        
        public void setNombreVoituresVendues(int nombreVoituresVendues) {
            this.nombreVoituresVendues = nombreVoituresVendues;
        }
        
        public int getNombreVoituresEnCours() {
            return nombreVoituresEnCours;
        }
        
        public void setNombreVoituresEnCours(int nombreVoituresEnCours) {
            this.nombreVoituresEnCours = nombreVoituresEnCours;
        }
        
        public double getCoutTotalImportation() {
            return coutTotalImportation;
        }
        
        public void setCoutTotalImportation(double coutTotalImportation) {
            this.coutTotalImportation = coutTotalImportation;
        }
        
        public double getRevenuTotal() {
            return revenuTotal;
        }
        
        public void setRevenuTotal(double revenuTotal) {
            this.revenuTotal = revenuTotal;
        }
        
        public double getBeneficeTotal() {
            return beneficeTotal;
        }
        
        public void setBeneficeTotal(double beneficeTotal) {
            this.beneficeTotal = beneficeTotal;
        }
        
        public double getMargeMoyenne() {
            return margeMoyenne;
        }
        
        public void setMargeMoyenne(double margeMoyenne) {
            this.margeMoyenne = margeMoyenne;
        }
    }
}

