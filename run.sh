#!/bin/bash

# Script de demarrage de l'application Import Voitures Guinee

# Verifier Java
if ! command -v java &> /dev/null; then
    echo "Erreur: Java n'est pas installe ou non accessible"
    exit 1
fi

# Repertoires
JAVAFX_DIR="lib/javafx-sdk-21"
JDBC_JAR="lib/sqlite-jdbc-3.44.1.0.jar"
LIB_JARS="lib/*"
SRC_DIR="src/main/java"
RESOURCES_DIR="src/main/resources"
BIN_DIR="bin"

# Verifier la presence de JavaFX
if [ ! -d "$JAVAFX_DIR" ]; then
    echo "Erreur: JavaFX SDK non trouve dans $JAVAFX_DIR"
    exit 1
fi

# Verifier la presence du driver JDBC
if [ ! -f "$JDBC_JAR" ]; then
    echo "Erreur: SQLite JDBC non trouve dans $JDBC_JAR"
    exit 1
fi

# Creer le repertoire bin s'il n'existe pas
mkdir -p "$BIN_DIR"

echo ""
echo "========================================"
echo "Compilation du projet..."
echo "========================================"
echo ""

# Compiler
javac -cp "$JAVAFX_DIR/lib/*:$LIB_JARS" -d "$BIN_DIR" -sourcepath "$SRC_DIR" $(find "$SRC_DIR" -name "*.java") 2>/dev/null

if [ $? -ne 0 ]; then
    echo "Erreur de compilation"
    exit 1
fi

# Copier les ressources (FXML, CSS, etc.) vers bin
if [ -d "$RESOURCES_DIR" ]; then
    cp -r "$RESOURCES_DIR/." "$BIN_DIR/"
fi

echo ""
echo "========================================"
echo "Demarrage de l'application..."
echo "========================================"
echo ""

# Executer l'application
java -cp "$BIN_DIR:$JAVAFX_DIR/lib/*:$LIB_JARS" \
     --module-path "$JAVAFX_DIR/lib" \
     --add-modules javafx.controls,javafx.fxml \
     com.importation.App

if [ $? -ne 0 ]; then
    echo "Erreur lors du demarrage de l'application"
    exit 1
fi

echo ""
echo "Application fermee normalement"
