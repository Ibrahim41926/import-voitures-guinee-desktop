package com.importation.models;

import java.time.LocalDate;

public class Paiement {
    private int id;
    private int voitureId;
    private int associeId;
    private double montant;
    private String devise; // CAD ou GNF
    private LocalDate datePaiement;
    private String typePaiement; // CONTRIBUTION, REVENU, REMBOURSEMENT
    private String description;

    // Constructeur vide
    public Paiement() {
    }

    // Constructeur avec parametres
    public Paiement(int voitureId, int associeId, double montant, String devise,
                    LocalDate datePaiement, String typePaiement, String description) {
        this.voitureId = voitureId;
        this.associeId = associeId;
        this.montant = montant;
        this.devise = devise;
        this.datePaiement = datePaiement;
        this.typePaiement = typePaiement;
        this.description = description;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVoitureId() {
        return voitureId;
    }

    public void setVoitureId(int voitureId) {
        this.voitureId = voitureId;
    }

    public int getAssocieId() {
        return associeId;
    }

    public void setAssocieId(int associeId) {
        this.associeId = associeId;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getTypePaiement() {
        return typePaiement;
    }

    public void setTypePaiement(String typePaiement) {
        this.typePaiement = typePaiement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return typePaiement + " - " + montant + " " + devise;
    }
}

