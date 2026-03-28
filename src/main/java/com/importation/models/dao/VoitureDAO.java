package com.importation.models.dao;

import com.importation.models.Voiture;
import com.importation.models.dao.controllers.utils.Constantes;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des voitures
 */
public class VoitureDAO {
    
    /**
     * Ajoute une nouvelle voiture
     */
    public static int ajouter(Voiture voiture) throws SQLException {
        String sql = "INSERT INTO VOITURE (marque, modele, annee, immatriculation, prixAchatCAD, " +
                     "transportCAD, assuranceCAD, dedouanementGNF, fraisDiversCAD, fraisDiversGNF, " +
                     "prixReventeGNF, tauxChangeCADGNF, dateImportation, statut, associeId) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = DatabaseConnection.obtenirConnexion().prepareStatement(sql)) {
            pstmt.setString(1, voiture.getMarque());
            pstmt.setString(2, voiture.getModele());
            pstmt.setInt(3, voiture.getAnnee());
            setNullableImmatriculation(pstmt, 4, voiture.getImmatriculation());
            pstmt.setDouble(5, voiture.getPrixAchatCAD());
            pstmt.setDouble(6, voiture.getTransportCAD());
            pstmt.setDouble(7, voiture.getAssuranceCAD());
            pstmt.setDouble(8, voiture.getDedouanementGNF());
            pstmt.setDouble(9, voiture.getFraisDiversCAD());
            pstmt.setDouble(10, voiture.getFraisDiversGNF());
            pstmt.setDouble(11, voiture.getPrixReventeGNF());
            pstmt.setDouble(12, voiture.getTauxChangeCADGNF());
            pstmt.setDate(13, Date.valueOf(voiture.getDateImportation()));
            pstmt.setString(14, voiture.getStatut() != null ? voiture.getStatut() : Constantes.STATUT_EN_COURS);
            pstmt.setInt(15, voiture.getAssocieId());
            
            pstmt.executeUpdate();
            
            // Récuperer l'ID généré avec last_insert_rowid()
            try (Statement stmt = DatabaseConnection.obtenirConnexion().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid() as id")) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1;
    }
    
    /**
     * Met à  jour une voiture existante
     */
    public static void mettre_a_jour(Voiture voiture) throws SQLException {
        String sql = "UPDATE VOITURE SET marque=?, modele=?, annee=?, immatriculation=?, " +
                     "prixAchatCAD=?, transportCAD=?, assuranceCAD=?, dedouanementGNF=?, " +
                     "fraisDiversCAD=?, fraisDiversGNF=?, prixReventeGNF=?, tauxChangeCADGNF=?, dateImportation=?, " +
                     "statut=?, associeId=? WHERE id=?";
        
        try (PreparedStatement pstmt = DatabaseConnection.obtenirConnexion().prepareStatement(sql)) {
            pstmt.setString(1, voiture.getMarque());
            pstmt.setString(2, voiture.getModele());
            pstmt.setInt(3, voiture.getAnnee());
            setNullableImmatriculation(pstmt, 4, voiture.getImmatriculation());
            pstmt.setDouble(5, voiture.getPrixAchatCAD());
            pstmt.setDouble(6, voiture.getTransportCAD());
            pstmt.setDouble(7, voiture.getAssuranceCAD());
            pstmt.setDouble(8, voiture.getDedouanementGNF());
            pstmt.setDouble(9, voiture.getFraisDiversCAD());
            pstmt.setDouble(10, voiture.getFraisDiversGNF());
            pstmt.setDouble(11, voiture.getPrixReventeGNF());
            pstmt.setDouble(12, voiture.getTauxChangeCADGNF());
            pstmt.setDate(13, Date.valueOf(voiture.getDateImportation()));
            pstmt.setString(14, voiture.getStatut());
            pstmt.setInt(15, voiture.getAssocieId());
            pstmt.setInt(16, voiture.getId());
            
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Supprime une voiture
     */
    public static void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM VOITURE WHERE id=?";
        
        try (PreparedStatement pstmt = DatabaseConnection.obtenirConnexion().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Récuperé une voiture par son ID
     */
    public static Voiture obtenirParId(int id) throws SQLException {
        String sql = "SELECT * FROM VOITURE WHERE id=?";
        
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
     * Récupére toutes les voitures
     */
    public static List<Voiture> obtenirTous() throws SQLException {
        String sql = "SELECT * FROM VOITURE ORDER BY dateImportation DESC";
        List<Voiture> voitures = new ArrayList<>();
        
        try (Statement stmt = DatabaseConnection.obtenirConnexion().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                voitures.add(mapperResultSet(rs));
            }
        }
        return voitures;
    }
    
    /**
     * Récupére les voitures par statut
     */
    public static List<Voiture> obtenirParStatut(String statut) throws SQLException {
        String sql = "SELECT * FROM VOITURE WHERE statut=? ORDER BY dateImportation DESC";
        List<Voiture> voitures = new ArrayList<>();
        
        try (PreparedStatement pstmt = DatabaseConnection.obtenirConnexion().prepareStatement(sql)) {
            pstmt.setString(1, statut);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    voitures.add(mapperResultSet(rs));
                }
            }
        }
        return voitures;
    }
    
    /**
     * Récupére les voitures d'un associé
     */
    public static List<Voiture> obtenirParAssocie(int associeId) throws SQLException {
        String sql = "SELECT * FROM VOITURE WHERE associeId=? ORDER BY dateImportation DESC";
        List<Voiture> voitures = new ArrayList<>();
        
        try (PreparedStatement pstmt = DatabaseConnection.obtenirConnexion().prepareStatement(sql)) {
            pstmt.setInt(1, associeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    voitures.add(mapperResultSet(rs));
                }
            }
        }
        return voitures;
    }
    
    /**
     * Mapper un ResultSet vers un objet Voiture
     */
    private static Voiture mapperResultSet(ResultSet rs) throws SQLException {
        Voiture voiture = new Voiture();
        voiture.setId(rs.getInt("id"));
        voiture.setMarque(rs.getString("marque"));
        voiture.setModele(rs.getString("modele"));
        voiture.setAnnee(rs.getInt("annee"));
        voiture.setImmatriculation(rs.getString("immatriculation"));
        voiture.setPrixAchatCAD(rs.getDouble("prixAchatCAD"));
        voiture.setTransportCAD(rs.getDouble("transportCAD"));
        voiture.setAssuranceCAD(rs.getDouble("assuranceCAD"));
        voiture.setDedouanementGNF(rs.getDouble("dedouanementGNF"));
        voiture.setFraisDiversCAD(rs.getDouble("fraisDiversCAD"));
        voiture.setFraisDiversGNF(rs.getDouble("fraisDiversGNF"));
        voiture.setPrixReventeGNF(rs.getDouble("prixReventeGNF"));
        voiture.setTauxChangeCADGNF(rs.getDouble("tauxChangeCADGNF"));
        voiture.setDateImportation(rs.getDate("dateImportation").toLocalDate());
        voiture.setStatut(rs.getString("statut"));
        voiture.setAssocieId(rs.getInt("associeId"));
        
        return voiture;
    }

    private static void setNullableImmatriculation(PreparedStatement pstmt, int index, String immatriculation) throws SQLException {
        if (immatriculation == null || immatriculation.trim().isEmpty()) {
            pstmt.setNull(index, Types.VARCHAR);
            return;
        }
        pstmt.setString(index, immatriculation.trim());
    }
}

