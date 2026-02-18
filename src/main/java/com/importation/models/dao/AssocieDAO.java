package com.importation.models.dao;

import com.importation.models.Associe;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des associés
 */
public class AssocieDAO {
    
    /**
     * Ajoute un nouvel associé
     */
    public static int ajouter(Associe associe) throws SQLException {
        String sql = "INSERT INTO ASSOCIE (nom, prenom, telephone, email, pourcentageParticipation) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = DatabaseConnection.obtenirConnexion().prepareStatement(sql)) {
            pstmt.setString(1, associe.getNom());
            pstmt.setString(2, associe.getPrenom());
            pstmt.setString(3, associe.getTelephone());
            pstmt.setString(4, associe.getEmail());
            pstmt.setDouble(5, associe.getPourcentageParticipation());
            
            pstmt.executeUpdate();
            
            // Récupérer l'ID généré avec last_insert_rowid()
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
     * Met à  jour un associé existant
     */
    public static void mettre_a_jour(Associe associe) throws SQLException {
        String sql = "UPDATE ASSOCIE SET nom=?, prenom=?, telephone=?, email=?, " +
                     "pourcentageParticipation=? WHERE id=?";
        
        try (PreparedStatement pstmt = DatabaseConnection.obtenirConnexion().prepareStatement(sql)) {
            pstmt.setString(1, associe.getNom());
            pstmt.setString(2, associe.getPrenom());
            pstmt.setString(3, associe.getTelephone());
            pstmt.setString(4, associe.getEmail());
            pstmt.setDouble(5, associe.getPourcentageParticipation());
            pstmt.setInt(6, associe.getId());
            
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Supprime un associé par son ID
     */
    public static void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM ASSOCIE WHERE id=?";
        
        try (PreparedStatement pstmt = DatabaseConnection.obtenirConnexion().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Récupère un associé par son ID
     */
    public static Associe obtenirParId(int id) throws SQLException {
        String sql = "SELECT * FROM ASSOCIE WHERE id=?";
        
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
     * Récupère tous les associés
     */
    public static List<Associe> obtenirTous() throws SQLException {
        String sql = "SELECT * FROM ASSOCIE ORDER BY prenom, nom";
        List<Associe> associes = new ArrayList<>();
        
        try (Statement stmt = DatabaseConnection.obtenirConnexion().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                associes.add(mapperResultSet(rs));
            }
        }
        return associes;
    }
    
    /**
     * Mapper un ResultSet vers un objet Associe
     */
    private static Associe mapperResultSet(ResultSet rs) throws SQLException {
        Associe associe = new Associe();
        associe.setId(rs.getInt("id"));
        associe.setNom(rs.getString("nom"));
        associe.setPrenom(rs.getString("prenom"));
        associe.setTelephone(rs.getString("telephone"));
        associe.setEmail(rs.getString("email"));
        associe.setPourcentageParticipation(rs.getDouble("pourcentageParticipation"));
        
        return associe;
    }
}

