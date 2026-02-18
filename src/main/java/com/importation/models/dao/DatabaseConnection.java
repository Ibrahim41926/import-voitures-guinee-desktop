package com.importation.models.dao;

import java.sql.*;
import com.importation.models.dao.controllers.utils.Constantes;

/**
 * Classe pour générer la connexion à la base de données SQLite
 */
public class DatabaseConnection {
    
    private static Connection connexion;
    
    /**
     * Etablir une connexion à  la base de données
     */
    public static Connection obtenirConnexion() throws SQLException {
        if (connexion == null || connexion.isClosed()) {
            try {
                Class.forName(Constantes.DB_DRIVER);
                connexion = DriverManager.getConnection(Constantes.DB_URL);
                initialiserBaseDeDonnees();
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver SQLite non trouvÃƒÆ’Ã‚Â©", e);
            }
        }
        return connexion;
    }
    
    /**
     * Initialise les tables si elles n'existent pas
     */
    private static void initialiserBaseDeDonnees() throws SQLException {
        String[] requetes = {
            // Table ASSOCIE
            "CREATE TABLE IF NOT EXISTS ASSOCIE (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nom VARCHAR(100) NOT NULL, " +
            "prenom VARCHAR(100) NOT NULL, " +
            "telephone VARCHAR(20), " +
            "email VARCHAR(100), " +
            "pourcentageParticipation DECIMAL(5,2) NOT NULL)",
            
            // Table VOITURE
            "CREATE TABLE IF NOT EXISTS VOITURE (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "marque VARCHAR(50) NOT NULL, " +
            "modele VARCHAR(50) NOT NULL, " +
            "annee INTEGER NOT NULL, " +
            "immatriculation VARCHAR(20) UNIQUE, " +
            "prixAchatCAD DECIMAL(10,2) NOT NULL, " +
            "transportCAD DECIMAL(10,2), " +
            "assuranceCAD DECIMAL(10,2), " +
            "dedouanementGNF DECIMAL(15,2), " +
            "fraisDiversCAD DECIMAL(10,2), " +
            "fraisDiversGNF DECIMAL(15,2), " +
            "prixReventeGNF DECIMAL(15,2), " +
            "dateImportation DATE NOT NULL, " +
            "statut VARCHAR(20) NOT NULL, " +
            "associeId INTEGER, " +
            "FOREIGN KEY(associeId) REFERENCES ASSOCIE(id))",
            
            // Table FRAIS
            "CREATE TABLE IF NOT EXISTS FRAIS (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "voitureId INTEGER NOT NULL, " +
            "description VARCHAR(255), " +
            "montant DECIMAL(10,2) NOT NULL, " +
            "devise VARCHAR(3) NOT NULL, " +
            "dateDepense DATE NOT NULL, " +
            "categorie VARCHAR(50) NOT NULL, " +
            "FOREIGN KEY(voitureId) REFERENCES VOITURE(id))",
            
            // Table PAIEMENT
            "CREATE TABLE IF NOT EXISTS PAIEMENT (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "voitureId INTEGER, " +
            "associeId INTEGER NOT NULL, " +
            "montant DECIMAL(15,2) NOT NULL, " +
            "devise VARCHAR(3) NOT NULL, " +
            "datePaiement DATE NOT NULL, " +
            "typePaiement VARCHAR(50) NOT NULL, " +
            "description VARCHAR(255), " +
            "FOREIGN KEY(voitureId) REFERENCES VOITURE(id), " +
            "FOREIGN KEY(associeId) REFERENCES ASSOCIE(id))",

            // Table CONFIGURATION (cle/valeur)
            "CREATE TABLE IF NOT EXISTS CONFIGURATION (" +
            "cle VARCHAR(100) PRIMARY KEY, " +
            "valeur DECIMAL(15,4) NOT NULL)",

            // Taux de change par defaut si absent
            "INSERT OR IGNORE INTO CONFIGURATION (cle, valeur) VALUES ('TAUX_CHANGE', " + Constantes.TAUX_CHANGE_DEFAUT + ")"
        };
        
        try (Statement stmt = connexion.createStatement()) {
            for (String requete : requetes) {
                stmt.execute(requete);
            }
        }
    }
    
    /**
     * Ferme la connexion ÃƒÆ’Ã‚Â  la base de donnÃƒÆ’Ã‚Â©es
     */
    public static void fermerConnexion() throws SQLException {
        if (connexion != null && !connexion.isClosed()) {
            connexion.close();
            connexion = null;
        }
    }
    
    /**
     * Teste la connexion
     */
    public static boolean testerConnexion() {
        try {
            obtenirConnexion();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

