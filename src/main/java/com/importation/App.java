package com.importation;

import com.importation.models.dao.DatabaseConnection;
import com.importation.models.dao.controllers.utils.ConvertisseurDevise;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principale de l'application JavaFX.
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            // Initialiser la base.
            if (!DatabaseConnection.testerConnexion()) {
                System.err.println("Erreur : impossible de se connecter a la base de donnees");
                System.exit(1);
            }

            // Charger le taux de change persistant avant l'ouverture des ecrans.
            ConvertisseurDevise.initialiserDepuisBase();

            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/importation/models/resources/fxml/main.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root, 1200, 800);
            String css = getClass().getResource("/com/importation/models/resources/fxml/css/style.css").toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setTitle("Gestion Importation Voitures Canada -> Guinee");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void stop() throws Exception {
        DatabaseConnection.fermerConnexion();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

