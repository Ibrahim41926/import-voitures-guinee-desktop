package com.importation.models.dao;

import com.importation.models.Paiement;
import com.importation.models.dao.controllers.utils.ConvertisseurDevise;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des paiements.
 */
public class PaimentDAO {

    /**
     * Ajoute un nouveau paiement.
     */
    public static int ajouter(Paiement paiement) throws SQLException {
        String sql = "INSERT INTO PAIEMENT (voitureId, associeId, montant, devise, "
            + "datePaiement, typePaiement, description) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection connexion = DatabaseConnection.obtenirConnexion();
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            pstmt.setObject(1, paiement.getVoitureId() > 0 ? paiement.getVoitureId() : null);
            pstmt.setInt(2, paiement.getAssocieId());
            pstmt.setDouble(3, paiement.getMontant());
            pstmt.setString(4, paiement.getDevise());
            pstmt.setDate(5, Date.valueOf(paiement.getDatePaiement()));
            pstmt.setString(6, paiement.getTypePaiement());
            pstmt.setString(7, paiement.getDescription());
            pstmt.executeUpdate();
        }

        try (Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid() as id")) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }

    /**
     * Met a jour un paiement existant.
     */
    public static void mettre_a_jour(Paiement paiement) throws SQLException {
        String sql = "UPDATE PAIEMENT SET voitureId=?, associeId=?, montant=?, devise=?, "
            + "datePaiement=?, typePaiement=?, description=? WHERE id=?";

        try (PreparedStatement pstmt = DatabaseConnection.obtenirConnexion().prepareStatement(sql)) {
            pstmt.setObject(1, paiement.getVoitureId() > 0 ? paiement.getVoitureId() : null);
            pstmt.setInt(2, paiement.getAssocieId());
            pstmt.setDouble(3, paiement.getMontant());
            pstmt.setString(4, paiement.getDevise());
            pstmt.setDate(5, Date.valueOf(paiement.getDatePaiement()));
            pstmt.setString(6, paiement.getTypePaiement());
            pstmt.setString(7, paiement.getDescription());
            pstmt.setInt(8, paiement.getId());
            pstmt.executeUpdate();
        }
    }

    /**
     * Supprime un paiement.
     */
    public static void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM PAIEMENT WHERE id=?";
        try (PreparedStatement pstmt = DatabaseConnection.obtenirConnexion().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * Recupere un paiement par son ID.
     */
    public static Paiement obtenirParId(int id) throws SQLException {
        String sql = "SELECT * FROM PAIEMENT WHERE id=?";
        try (PreparedStatement pstmt = DatabaseConnection.obtenirConnexion().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapperResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Recupere tous les paiements.
     */
    public static List<Paiement> obtenirTous() throws SQLException {
        String sql = "SELECT * FROM PAIEMENT ORDER BY datePaiement DESC";
        List<Paiement> paiements = new ArrayList<>();
        try (Statement stmt = DatabaseConnection.obtenirConnexion().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                paiements.add(mapperResultSet(rs));
            }
        }
        return paiements;
    }

    /**
     * Recupere les paiements d'une voiture.
     */
    public static List<Paiement> obtenirParVoiture(int voitureId) throws SQLException {
        String sql = "SELECT * FROM PAIEMENT WHERE voitureId=? ORDER BY datePaiement DESC";
        List<Paiement> paiements = new ArrayList<>();
        try (PreparedStatement pstmt = DatabaseConnection.obtenirConnexion().prepareStatement(sql)) {
            pstmt.setInt(1, voitureId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    paiements.add(mapperResultSet(rs));
                }
            }
        }
        return paiements;
    }

    /**
     * Recupere les paiements d'un associe.
     */
    public static List<Paiement> obtenirParAssocie(int associeId) throws SQLException {
        String sql = "SELECT * FROM PAIEMENT WHERE associeId=? ORDER BY datePaiement DESC";
        List<Paiement> paiements = new ArrayList<>();
        try (PreparedStatement pstmt = DatabaseConnection.obtenirConnexion().prepareStatement(sql)) {
            pstmt.setInt(1, associeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    paiements.add(mapperResultSet(rs));
                }
            }
        }
        return paiements;
    }

    /**
     * Calcule le total en GNF en tenant compte du taux courant.
     */
    public static double calculerSolde(int associeId) throws SQLException {
        String sql = "SELECT SUM(CASE WHEN devise='CAD' THEN montant * ? ELSE montant END) as total "
            + "FROM PAIEMENT WHERE associeId=?";

        try (PreparedStatement pstmt = DatabaseConnection.obtenirConnexion().prepareStatement(sql)) {
            pstmt.setDouble(1, ConvertisseurDevise.getTauxChange());
            pstmt.setInt(2, associeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0.0;
    }

    /**
     * Mappe un ResultSet vers un objet Paiement.
     */
    private static Paiement mapperResultSet(ResultSet rs) throws SQLException {
        Paiement paiement = new Paiement();
        paiement.setId(rs.getInt("id"));

        int voitureId = rs.getInt("voitureId");
        paiement.setVoitureId(rs.wasNull() ? 0 : voitureId);

        paiement.setAssocieId(rs.getInt("associeId"));
        paiement.setMontant(rs.getDouble("montant"));
        paiement.setDevise(rs.getString("devise"));
        paiement.setDatePaiement(rs.getDate("datePaiement").toLocalDate());
        paiement.setTypePaiement(rs.getString("typePaiement"));
        paiement.setDescription(rs.getString("description"));
        return paiement;
    }
}
