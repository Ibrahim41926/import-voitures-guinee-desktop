package com.importation.models.dao.controllers.utils;

import java.nio.file.Path;

public final class Constantes {
    public static final String APP_FOLDER_NAME = "ImportVoituresGuinee";
    public static final Path APP_HOME = construireAppHome();
    public static final Path DATA_DIRECTORY = APP_HOME.resolve("data");
    public static final Path DB_FILE = DATA_DIRECTORY.resolve("import_voitures.db");
    public static final Path LEGACY_DB_FILE = Path.of("database", "import_voitures.db").toAbsolutePath().normalize();
    public static final String DB_URL = "jdbc:sqlite:" + normaliserCheminSqlite(DB_FILE);
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

    // Categories de frais
    public static final String CATEGORIE_TRANSPORT = "TRANSPORT";
    public static final String CATEGORIE_ASSURANCE = "ASSURANCE";
    public static final String CATEGORIE_DEDOUANEMENT = "DEDOUANEMENT";
    public static final String CATEGORIE_DIVERS = "DIVERS";

    // Types de paiement
    public static final String TYPE_PAIEMENT_CONTRIBUTION = "CONTRIBUTION";
    public static final String TYPE_PAIEMENT_REVENU = "REVENU";
    public static final String TYPE_PAIEMENT_REMBOURSEMENT = "REMBOURSEMENT";

    // Messages
    public static final String MSG_SUCCES = "Operation reussie";
    public static final String MSG_ERREUR = "Une erreur s'est produite";
    public static final String MSG_VALIDATION_ERREUR = "Veuillez verifier vos donnees";

    // Pourcentage de participation par defaut (pour 3 associes)
    public static final double POURCENTAGE_PAR_DEFAUT = 33.33;

    private Constantes() {
    }

    private static Path construireAppHome() {
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData != null && !localAppData.isBlank()) {
            return Path.of(localAppData, APP_FOLDER_NAME).toAbsolutePath().normalize();
        }
        return Path.of(System.getProperty("user.home"), "." + APP_FOLDER_NAME).toAbsolutePath().normalize();
    }

    private static String normaliserCheminSqlite(Path path) {
        return path.toAbsolutePath().normalize().toString().replace('\\', '/');
    }
}
