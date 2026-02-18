package com.importation.models.dao.controllers.utils;

public class Constantes {
    // Chemins et configuration base de données
    public static final String DB_URL = "jdbc:sqlite:database/import_voitures.db";
    public static final String DB_DRIVER = "org.sqlite.JDBC";
    
    // Devises
    public static final String DEVISE_CAD = "CAD";
    public static final String DEVISE_GNF = "GNF";
    
    // Taux de change par defaut (1 CAD = 6400 GNF environ)
    public static final double TAUX_CHANGE_DEFAUT = 6400.0;
    
    // Statuts de voiture
    public static final String STATUT_EN_COURS = "EN_COURS";
    public static final String STATUT_VENDUE = "VENDUE";
    public static final String STATUT_SUSPENDUE = "SUSPENDUE";
    
    // Catégories de frais
    public static final String CATEGORIE_TRANSPORT = "TRANSPORT";
    public static final String CATEGORIE_ASSURANCE = "ASSURANCE";
    public static final String CATEGORIE_DEDOUANEMENT = "DEDOUANEMENT";
    public static final String CATEGORIE_DIVERS = "DIVERS";
    
    // Types de paiement
    public static final String TYPE_PAIEMENT_CONTRIBUTION = "CONTRIBUTION";
    public static final String TYPE_PAIEMENT_REVENU = "REVENU";
    public static final String TYPE_PAIEMENT_REMBOURSEMENT = "REMBOURSEMENT";
    
    // Messages
    public static final String MSG_SUCCES = "Opération réussie";
    public static final String MSG_ERREUR = "Une erreur s'est produite";
    public static final String MSG_VALIDATION_ERREUR = "Veuillez vérifier vos données";
    
    // Pourcentage de participation par défaut (pour 3 associés)
    public static final double POURCENTAGE_PAR_DEFAUT = 33.33;
}

