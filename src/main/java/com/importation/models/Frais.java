package com.importation.models;

import java.time.LocalDate;

public class Frais {
    private int id;
    private int voitureId;
    private String description;
    private double montant;
    private String devise; // CAD ou GNF
    private LocalDate dateDepense;
    private String categorie; // TRANSPORT, ASSURANCE, DEDOUANEMENT, DIVERS

    // Constructeur vide
    public Frais() {
    }

    // Constructeur avec parametres
    public Frais(int voitureId, String description, double montant, String devise, 
                 LocalDate dateDepense, String categorie) {
        this.voitureId = voitureId;
        this.description = description;
        this.montant = montant;
        this.devise = devise;
        this.dateDepense = dateDepense;
        this.categorie = categorie;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public LocalDate getDateDepense() {
        return dateDepense;
    }

    public void setDateDepense(LocalDate dateDepense) {
        this.dateDepense = dateDepense;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    @Override
    public String toString() {
        return categorie + " - " + montant + " " + devise;
    }
}

