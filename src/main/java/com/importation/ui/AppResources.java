package com.importation.ui;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

final class AppResources {
    private static final String[] LOGO_RESOURCE_PATHS = {
        "/LOGO.png",
        "/logo.png"
    };

    private static final Path[] LOGO_FILE_PATHS = {
        Path.of("src", "main", "images", "LOGO.png"),
        Path.of("src", "main", "images", "logo.png"),
        Path.of("images", "LOGO.png"),
        Path.of("images", "logo.png")
    };

    private AppResources() {
    }

    static Image loadLogoImage() {
        byte[] bytes = loadLogoBytes();
        if (bytes == null) {
            return null;
        }
        return new Image(new ByteArrayInputStream(bytes));
    }

    static byte[] loadLogoBytes() {
        for (String resourcePath : LOGO_RESOURCE_PATHS) {
            try (InputStream inputStream = AppResources.class.getResourceAsStream(resourcePath)) {
                if (inputStream != null) {
                    return inputStream.readAllBytes();
                }
            } catch (IOException ignored) {
                // Passe au candidat suivant.
            }
        }

        for (Path path : LOGO_FILE_PATHS) {
            if (Files.exists(path)) {
                try {
                    return Files.readAllBytes(path);
                } catch (IOException ignored) {
                    // Passe au candidat suivant.
                }
            }
        }

        return null;
    }
}
