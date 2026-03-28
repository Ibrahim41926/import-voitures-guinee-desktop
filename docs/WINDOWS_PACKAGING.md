# Packaging Windows

## Base locale

L'application stocke maintenant la base SQLite dans:

```text
%LOCALAPPDATA%\ImportVoituresGuinee\data\import_voitures.db
```

Au premier lancement, si une ancienne base existe encore dans:

```text
database\import_voitures.db
```

elle est recopied automatiquement vers `AppData` si la nouvelle base n'existe pas encore.

## Installateur Windows

Prerequis:

- JDK 17+ avec `JAVA_HOME`
- JavaFX SDK Windows dans `lib\javafx-sdk-21`
- WiX Toolset 3.x dans le `PATH`
- Maven dans le `PATH`

Commande:

```powershell
.\scripts\build-windows-installer.ps1
```

Sortie:

```text
target\jpackage\dist
```
