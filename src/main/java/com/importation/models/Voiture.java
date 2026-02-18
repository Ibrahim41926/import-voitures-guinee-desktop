package com.importation.models;

import java.time.LocalDate;

public class Voiture {
    private int id;
    private String marque;
    private String modele;
    private int annee;
    private String immatriculation;
    private double prixAchatCAD;
    private double transportCAD;
    private double assuranceCAD;
    private double dedouanementGNF;
    private double fraisDiversCAD;
    private double fraisDiversGNF;
    private double prixReventeGNF;
    private LocalDate dateImportation;
    private String statut; // EN_COURS, VENDUE, etc
    private int associeId;

    // Constructeur vide
    public Voiture() {
    }

    // Constructeur avec parametres
    public Voiture(String marque, String modele, int annee, String immatriculation,
                   double prixAchatCAD, double transportCAD, double assuranceCAD,
                   double dedouanementGNF, double fraisDiversCAD, double fraisDiversGNF,
                   double prixReventeGNF, LocalDate dateImportation, String statut, int associeId) {
        this.marque = marque;
        this.modele = modele;
        this.annee = annee;
        this.immatriculation = immatriculation;
        this.prixAchatCAD = prixAchatCAD;
        this.transportCAD = transportCAD;
        this.assuranceCAD = assuranceCAD;
        this.dedouanementGNF = dedouanementGNF;
        this.fraisDiversCAD = fraisDiversCAD;
        this.fraisDiversGNF = fraisDiversGNF;
        this.prixReventeGNF = prixReventeGNF;
        this.dateImportation = dateImportation;
        this.statut = statut;
        this.associeId = associeId;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public double getPrixAchatCAD() {
        return prixAchatCAD;
    }

    public void setPrixAchatCAD(double prixAchatCAD) {
        this.prixAchatCAD = prixAchatCAD;
    }

    public double getTransportCAD() {
        return transportCAD;
    }

    public void setTransportCAD(double transportCAD) {
        this.transportCAD = transportCAD;
    }

    public double getAssuranceCAD() {
        return assuranceCAD;
    }

    public void setAssuranceCAD(double assuranceCAD) {
        this.assuranceCAD = assuranceCAD;
    }

    public double getDedouanementGNF() {
        return dedouanementGNF;
    }

    public void setDedouanementGNF(double dedouanementGNF) {
        this.dedouanementGNF = dedouanementGNF;
    }

    public double getFraisDiversCAD() {
        return fraisDiversCAD;
    }

    public void setFraisDiversCAD(double fraisDiversCAD) {
        this.fraisDiversCAD = fraisDiversCAD;
    }

    public double getFraisDiversGNF() {
        return fraisDiversGNF;
    }

    public void setFraisDiversGNF(double fraisDiversGNF) {
        this.fraisDiversGNF = fraisDiversGNF;
    }

    public double getPrixReventeGNF() {
        return prixReventeGNF;
    }

    public void setPrixReventeGNF(double prixReventeGNF) {
        this.prixReventeGNF = prixReventeGNF;
    }

    public LocalDate getDateImportation() {
        return dateImportation;
    }

    public void setDateImportation(LocalDate dateImportation) {
        this.dateImportation = dateImportation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getAssocieId() {
        return associeId;
    }

    public void setAssocieId(int associeId) {
        this.associeId = associeId;
    }

    @Override
    public String toString() {
        return marque + " " + modele + " (" + annee + ")";
    }
}

