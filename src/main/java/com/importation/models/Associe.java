package com.importation.models;

public class Associe {
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private double pourcentageParticipation; // Pourcentage de participation aux bénefices

    // Constructeur vide
    public Associe() {
    }

    // Constructeur avec parametres
    public Associe(String nom, String prenom, String telephone, String email, double pourcentageParticipation) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.pourcentageParticipation = pourcentageParticipation;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getPourcentageParticipation() {
        return pourcentageParticipation;
    }

    public void setPourcentageParticipation(double pourcentageParticipation) {
        this.pourcentageParticipation = pourcentageParticipation;
    }

    @Override
    public String toString() {
        return prenom + " " + nom;
    }
}

