@echo off
REM Script d'installation de JavaFX SDK 21

setlocal enabledelayedexpansion

echo.
echo ========================================
echo Installation JavaFX SDK 21
echo ========================================
echo.

set JAVAFX_DIR=lib\javafx-sdk-21
set ZIP_FILE=lib\javafx-sdk-21.zip
set DOWNLOAD_URL=https://repo1.maven.org/maven2/org/openjfx/javafx-sdk/21.0.3/javafx-sdk-21.0.3-windows.zip

REM Créer le dossier lib
if not exist "lib" mkdir "lib"

REM Vérifier si JavaFX existe déjà
if exist "%JAVAFX_DIR%\lib" (
    echo JavaFX SDK 21 est deja installe
    dir "%JAVAFX_DIR%\lib" | find ".jar"
    echo.
    echo Installation complete!
    pause
    exit /b 0
)

REM Télécharger JavaFX
echo Telechargement de JavaFX SDK 21...
echo.

PowerShell -NoProfile -Command "^
  $ProgressPreference = 'SilentlyContinue'; ^
  [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12; ^
  $client = New-Object System.Net.WebClient; ^
  Write-Host 'Telechargement en cours...' ; ^
  $client.DownloadFile('%DOWNLOAD_URL%', '%ZIP_FILE%'); ^
  if (Test-Path '%ZIP_FILE%') { Write-Host 'Telechargement reussi' } else { exit 1 }^
"

if %errorlevel% neq 0 (
    echo Erreur: Telechargement echoue
    echo.
    echo Telecharger manuellement depuis:
    echo %DOWNLOAD_URL%
    echo.
    pause
    exit /b 1
)

REM Extraire le fichier
echo.
echo Extraction du fichier...

PowerShell -NoProfile -Command "^
  Add-Type -AssemblyName System.IO.Compression.FileSystem; ^
  [System.IO.Compression.ZipFile]::ExtractToDirectory('%ZIP_FILE%', 'lib'); ^
  Write-Host 'Extraction complete'^
"

if %errorlevel% neq 0 (
    echo Erreur: Extraction echouee
    pause
    exit /b 1
)

REM Supprimer le ZIP
echo.
echo Nettoyage...
del "%ZIP_FILE%"

REM Vérifier l'installation
echo.
echo Verification...
if exist "%JAVAFX_DIR%\lib" (
    echo JavaFX SDK 21 a ete installe avec succes!
    echo.
    echo Chemin: %JAVAFX_DIR%
    echo.
    echo Vous pouvez maintenant lancer l'application avec:
    echo   run.bat
) else (
    echo Erreur: JavaFX SDK n'a pas ete installe correctement
    pause
    exit /b 1
)

echo.
pause
