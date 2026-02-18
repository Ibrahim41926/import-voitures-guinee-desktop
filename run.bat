@echo off
title Import Voitures Guinee
color 0A
setlocal EnableDelayedExpansion

echo ========================================
echo    IMPORT VOITURES GUINEE - CANADA
echo ========================================
echo.

REM === VERIFICATION DE JAVA ===
echo [1/5] Verification de Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERREUR] Java n'est pas installe ou non accessible
    pause
    exit /b 1
)

REM === DEFINIR LES REPERTOIRES ===
set JAVAFX_DIR=lib\javafx-sdk-21
set LIB_DIR=lib
set SRC_DIR=src\main\java
set BIN_DIR=bin
set RESOURCES_DIR=src\main\resources

echo [OK] Java detecte
echo.

REM === VERIFIER LES DEPENDANCES ===
echo [2/5] Verification des dependances...
if not exist "%JAVAFX_DIR%" (
    echo [ERREUR] JavaFX SDK non trouve dans %JAVAFX_DIR%
    pause
    exit /b 1
)
if not exist "%LIB_DIR%\sqlite-jdbc-3.44.1.0.jar" (
    echo [ERREUR] SQLite JDBC non trouve dans %LIB_DIR%
    pause
    exit /b 1
)
echo [OK] Dependances trouvees
echo.

REM === PREPARATION ===
echo [3/5] Preparation du repertoire bin...
if exist "%BIN_DIR%" rmdir /s /q "%BIN_DIR%" 2>nul
mkdir "%BIN_DIR%" 2>nul
echo [OK] Repertoire bin pret
echo.

REM === COMPILATION ===
echo [4/5] Compilation du projet en cours...
set "CLASSPATH=%JAVAFX_DIR%\lib\*;%LIB_DIR%\*"
set "JAVA_FILES="
for /R "%SRC_DIR%" %%F in (*.java) do set "JAVA_FILES=!JAVA_FILES! "%%F""

javac -encoding UTF-8 -cp "%CLASSPATH%" -d "%BIN_DIR%" !JAVA_FILES! 2> compile_errors.log
if %errorlevel% neq 0 (
    echo [ERREUR] Echec de la compilation
    echo.
    type compile_errors.log
    pause
    exit /b 1
)
echo [OK] Compilation reussie
echo.

REM === COPIER LES RESSOURCES ===
echo [5/5] Copie des ressources...
if exist "%RESOURCES_DIR%" (
    xcopy "%RESOURCES_DIR%" "%BIN_DIR%\" /E /I /Y >nul 2>&1
    echo [OK] Ressources copiees
) else (
    echo [INFO] Aucune ressource a copier
)

echo.
echo ========================================
echo    LANCEMENT DE L'APPLICATION
echo ========================================
echo.

java -cp "%BIN_DIR%;%CLASSPATH%" ^
     --module-path "%JAVAFX_DIR%\lib" ^
     --add-modules javafx.controls,javafx.fxml ^
     com.importation.App

if %errorlevel% neq 0 (
    echo.
    echo [ERREUR] L'application s'est arretee anormalement
    pause
    exit /b 1
)

echo.
echo [SUCCES] Application fermee normalement
pause
