package com.importation.models.dao;

import com.importation.models.dao.controllers.utils.Constantes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe pour generer la connexion a la base de donnees SQLite.
 */
public class DatabaseConnection {

    private static Connection connexion;

    /**
     * Etablit une connexion a la base de donnees.
     */
    public static Connection obtenirConnexion() throws SQLException {
        if (connexion == null || connexion.isClosed()) {
            try {
                Class.forName(Constantes.DB_DRIVER);
                preparerEmplacementBase();
                connexion = DriverManager.getConnection(Constantes.DB_URL);
                initialiserBaseDeDonnees();
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver SQLite non trouve", e);
            }
        }
        return connexion;
    }

    /**
     * Initialise les tables si elles n'existent pas.
     */
    private static void initialiserBaseDeDonnees() throws SQLException {
        String[] requetes = {
            "CREATE TABLE IF NOT EXISTS ASSOCIE ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nom VARCHAR(100) NOT NULL, "
                + "prenom VARCHAR(100) NOT NULL, "
                + "telephone VARCHAR(20), "
                + "email VARCHAR(100), "
                + "pourcentageParticipation DECIMAL(5,2) NOT NULL)",

            "CREATE TABLE IF NOT EXISTS VOITURE ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "marque VARCHAR(50) NOT NULL, "
                + "modele VARCHAR(50) NOT NULL, "
                + "annee INTEGER NOT NULL, "
                + "immatriculation VARCHAR(20) UNIQUE, "
                + "prixAchatCAD DECIMAL(10,2) NOT NULL, "
                + "transportCAD DECIMAL(10,2), "
                + "assuranceCAD DECIMAL(10,2), "
                + "dedouanementGNF DECIMAL(15,2), "
                + "fraisDiversCAD DECIMAL(10,2), "
                + "fraisDiversGNF DECIMAL(15,2), "
                + "prixReventeGNF DECIMAL(15,2), "
                + "dateImportation DATE NOT NULL, "
                + "statut VARCHAR(20) NOT NULL, "
                + "associeId INTEGER, "
                + "FOREIGN KEY(associeId) REFERENCES ASSOCIE(id))",

            "CREATE TABLE IF NOT EXISTS FRAIS ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "voitureId INTEGER NOT NULL, "
                + "description VARCHAR(255), "
                + "montant DECIMAL(10,2) NOT NULL, "
                + "devise VARCHAR(3) NOT NULL, "
                + "dateDepense DATE NOT NULL, "
                + "categorie VARCHAR(50) NOT NULL, "
                + "FOREIGN KEY(voitureId) REFERENCES VOITURE(id))",

            "CREATE TABLE IF NOT EXISTS PAIEMENT ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "voitureId INTEGER, "
                + "associeId INTEGER NOT NULL, "
                + "montant DECIMAL(15,2) NOT NULL, "
                + "devise VARCHAR(3) NOT NULL, "
                + "datePaiement DATE NOT NULL, "
                + "typePaiement VARCHAR(50) NOT NULL, "
                + "description VARCHAR(255), "
                + "FOREIGN KEY(voitureId) REFERENCES VOITURE(id), "
                + "FOREIGN KEY(associeId) REFERENCES ASSOCIE(id))",

            "CREATE TABLE IF NOT EXISTS CONFIGURATION ("
                + "cle VARCHAR(100) PRIMARY KEY, "
                + "valeur DECIMAL(15,4) NOT NULL)",

            "INSERT OR IGNORE INTO CONFIGURATION (cle, valeur) VALUES ('TAUX_CHANGE', "
                + Constantes.TAUX_CHANGE_DEFAUT + ")"
        };

        try (Statement stmt = connexion.createStatement()) {
            for (String requete : requetes) {
                stmt.execute(requete);
            }
        }

        ajouterColonneTauxChangeVoitureSiAbsente();
        initialiserTauxChangeVoituresSansValeur();
    }

    /**
     * Ferme la connexion a la base de donnees.
     */
    public static void fermerConnexion() throws SQLException {
        if (connexion != null && !connexion.isClosed()) {
            connexion.close();
            connexion = null;
        }
    }

    /**
     * Teste la connexion.
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

    private static void preparerEmplacementBase() throws SQLException {
        try {
            Files.createDirectories(Constantes.DATA_DIRECTORY);
            migrerAncienneBaseSiNecessaire();
        } catch (IOException e) {
            throw new SQLException("Impossible de preparer le dossier de donnees: " + Constantes.DATA_DIRECTORY, e);
        }
    }

    private static void migrerAncienneBaseSiNecessaire() throws IOException {
        Path cible = Constantes.DB_FILE;
        if (Files.exists(cible)) {
            return;
        }

        Path legacy = Constantes.LEGACY_DB_FILE;
        if (!Files.isRegularFile(legacy) || legacy.equals(cible)) {
            return;
        }

        Files.copy(legacy, cible, StandardCopyOption.COPY_ATTRIBUTES);
        copierFichierSQLiteCompagnonSiPresent(legacy, cible, "-wal");
        copierFichierSQLiteCompagnonSiPresent(legacy, cible, "-shm");
        copierFichierSQLiteCompagnonSiPresent(legacy, cible, "-journal");
    }

    private static void copierFichierSQLiteCompagnonSiPresent(Path sourceDb, Path cibleDb, String suffixe) throws IOException {
        Path source = Path.of(sourceDb.toString() + suffixe);
        if (!Files.isRegularFile(source)) {
            return;
        }
        Files.copy(source, Path.of(cibleDb.toString() + suffixe), StandardCopyOption.COPY_ATTRIBUTES);
    }

    private static void ajouterColonneTauxChangeVoitureSiAbsente() throws SQLException {
        if (colonneExiste("VOITURE", "tauxChangeCADGNF")) {
            return;
        }

        try (Statement stmt = connexion.createStatement()) {
            stmt.execute("ALTER TABLE VOITURE ADD COLUMN tauxChangeCADGNF DECIMAL(15,4)");
        }
    }

    private static boolean colonneExiste(String table, String colonne) throws SQLException {
        try (Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                if (colonne.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void initialiserTauxChangeVoituresSansValeur() throws SQLException {
        String sql = "UPDATE VOITURE SET tauxChangeCADGNF = ? WHERE tauxChangeCADGNF IS NULL OR tauxChangeCADGNF <= 0";
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            pstmt.setDouble(1, obtenirTauxChangeConfigure());
            pstmt.executeUpdate();
        }
    }

    private static double obtenirTauxChangeConfigure() throws SQLException {
        String sql = "SELECT valeur FROM CONFIGURATION WHERE cle = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            pstmt.setString(1, "TAUX_CHANGE");
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double valeur = rs.getDouble("valeur");
                    if (valeur > 0) {
                        return valeur;
                    }
                }
            }
        }
        return Constantes.TAUX_CHANGE_DEFAUT;
    }
}
