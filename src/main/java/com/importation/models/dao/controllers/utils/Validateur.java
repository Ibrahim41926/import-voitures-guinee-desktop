package com.importation.models.dao.controllers.utils;

public class Validateur {
    
    /**
     * Valide si une chaÃƒÆ’Ã‚Â®ne est vide ou nulle
     */
    public static boolean estVide(String texte) {
        return texte == null || texte.trim().isEmpty();
    }
    
    /**
     * Valide si une chaÃƒÆ’Ã‚Â®ne est un email
     */
    public static boolean estEmailValide(String email) {
        if (estVide(email)) {
            return false;
        }
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }
    
    /**
     * Valide si une chaÃƒÆ’Ã‚Â®ne contient uniquement des chiffres
     */
    public static boolean estNumerique(String texte) {
        if (estVide(texte)) {
            return false;
        }
        try {
            Double.parseDouble(texte);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Valide si un nombre est positif
     */
    public static boolean estPositif(double nombre) {
        return nombre > 0;
    }
    
    /**
     * Valide si un nombre est non-nÃƒÆ’Ã‚Â©gatif
     */
    public static boolean estNonNegatif(double nombre) {
        return nombre >= 0;
    }
    
    /**
     * Valide un numÃƒÆ’Ã‚Â©ro de tÃƒÆ’Ã‚Â©lÃƒÆ’Ã‚Â©phone (format simple)
     */
    public static boolean estTelephoneValide(String telephone) {
        if (estVide(telephone)) {
            return false;
        }
        // Accepte numÃƒÆ’Ã‚Â©ros avec ou sans espaces, tirets, etc.
        String regex = "^[0-9\\s\\-\\+\\(\\)]{7,}$";
        return telephone.matches(regex);
    }
    
    /**
     * Valide un numÃƒÆ’Ã‚Â©ro d'immatriculation
     */
    public static boolean estImmatriculationValide(String immatriculation) {
        if (estVide(immatriculation)) {
            return false;
        }
        // Format simple : lettres et chiffres, 3-10 caractÃƒÆ’Ã‚Â¨res
        return immatriculation.matches("^[A-Z0-9\\-]{3,10}$");
    }
    
    /**
     * Valide une annÃƒÆ’Ã‚Â©e de voiture
     */
    public static boolean estAnneeValide(int annee) {
        int anneeActuelle = java.time.Year.now().getValue();
        return annee >= 1900 && annee <= anneeActuelle;
    }
    
    /**
     * Valide un pourcentage (0-100)
     */
    public static boolean estPourcentageValide(double pourcentage) {
        return pourcentage >= 0 && pourcentage <= 100;
    }
}

