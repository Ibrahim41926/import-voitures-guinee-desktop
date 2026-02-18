# Gestion Importation Voitures Canada → Guinée

Application JavaFX pour gérer l'importation et la vente de voitures du Canada vers la Guinée avec gestion des associés et des bénéfices.

## 📋 Fonctionnalités

### Gestion des Voitures (CRUD)
- ✅ Ajouter, modifier, supprimer des voitures
- ✅ Suivi des frais d'importation (Canada et Guinée)
- ✅ Gestion du prix de revente
- ✅ Filtrage par statut (EN_COURS, VENDUE, SUSPENDUE)
- ✅ Affichage de la liste complète des véhicules

### Calculs Automatiques
- ✅ **Coût total d'importation** = (Prix achat + Transport + Assurance + Frais divers Canada) converti en GNF + Dédouanement + Frais divers Guinée
- ✅ **Bénéfice net** = Prix de revente - Coût total
- ✅ **Marge bénéficiaire** = (Bénéfice / Coût total) × 100%
- ✅ **Conversion automatique CAD ↔ GNF** avec taux paramétrable

### Gestion des Associés
- ✅ Gestion de 3 associés maximum
- ✅ Suivi des contributions de chacun
- ✅ Répartition automatique des bénéfices par pourcentage
- ✅ Calcul du solde net par associé

### Interface Utilisateur
- ✅ Interface simple et intuitive en français
- ✅ Tableau de bord récapitulatif
- ✅ Liste des voitures avec détails
- ✅ Formulaire d'ajout/modification avec calculs en temps réel
- ✅ Gestion des associés

### Données
- ✅ Base de données SQLite locale
- ✅ Persistance des données

## 🏗️ Architecture

### Structure du Projet
```
src/main/java/com/importation/
├── App.java                          # Classe principale de l'application
├── models/
│   ├── Voiture.java                  # Modèle de voiture
│   ├── Associe.java                  # Modèle d'associé
│   ├── Frais.java                    # Modèle de frais
│   ├── Paiement.java                 # Modèle de paiement
│   └── dao/
│       ├── DatabaseConnection.java   # Gestion de la BD
│       ├── VoitureDAO.java          # DAO pour les voitures
│       ├── AssocieDAO.java          # DAO pour les associés
│       ├── PaimentDAO.java          # DAO pour les paiements
│       └── controllers/
│           ├── VoitureController.java      # Contrôleur voitures
│           ├── AssocieController.java      # Contrôleur associés
│           ├── CalculController.java       # Contrôleur calculs
│           └── utils/
│               ├── Constantes.java         # Constantes de l'app
│               ├── Validateur.java         # Validations
│               └── ConvertisseurDevise.java # Conversion CAD/GNF
└── ui/
    ├── MainController.java           # Contrôleur principal UI
    ├── VoitureUIController.java      # UI des voitures
    └── AssocieUIController.java      # UI des associés

resources/
└── fxml/
    ├── main.fxml                     # Interface principale
    ├── voiture.fxml                  # Interface voitures
    ├── associe.fxml                  # Interface associés
    └── css/
        └── style.css                 # Styles de l'application

database/
└── import_voitures.db               # Base de données SQLite
```

### Pattern MVC
- **Models** : Voiture, Associe, Frais, Paiement
- **Views** : Fichiers FXML et CSS
- **Controllers** : VoitureController, AssocieController, CalculController
- **Data Access** : DAO pattern avec VoitureDAO, AssocieDAO, PaimentDAO

## 🚀 Installation et Démarrage

### Prérequis
- Java 21+ (JDK)
- JavaFX SDK 21
- Maven ou Gradle (optionnel)

### Étapes

1. **Cloner/Télécharger le projet**
   ```bash
   git clone <repository-url>
   cd import-voitures-guinee
   ```

2. **Compiler le projet**
   ```bash
   javac -cp "lib/javafx-sdk-21/lib/*:lib/sqlite-jdbc-3.44.1.0.jar" src/main/java/com/importation/*.java
   ```

3. **Lancer l'application**
   ```bash
   java -cp "lib/javafx-sdk-21/lib/*:lib/sqlite-jdbc-3.44.1.0.jar:src/main/java" --add-modules javafx.controls,javafx.fxml com.importation.App
   ```

## 📊 Champs de Données

### Voiture
- **Marque** : Marque du véhicule
- **Modèle** : Modèle du véhicule
- **Année** : Année de fabrication
- **Immatriculation** : Numéro d'immatriculation
- **Prix d'achat (CAD)** : Prix d'achat au Canada
- **Transport (CAD)** : Frais de transport Canada-Guinée
- **Assurance (CAD)** : Assurance du transport
- **Frais divers Canada (CAD)** : Autres frais au Canada
- **Dédouanement (GNF)** : Frais de dédouanement en Guinée
- **Frais divers Guinée (GNF)** : Autres frais en Guinée
- **Prix de revente (GNF)** : Prix de revente en Guinée
- **Date d'importation** : Date d'arrivée en Guinée
- **Statut** : EN_COURS / VENDUE / SUSPENDUE
- **Associé** : Associé responsable

### Associé
- **Prénom** : Prénom de l'associé
- **Nom** : Nom de l'associé
- **Téléphone** : Numéro de téléphone
- **Email** : Adresse email
- **% Participation** : Pourcentage de participation aux bénéfices

## 💰 Calculs

### Coût Total d'Importation (GNF)
```
Frais Canada (CAD):
  = Prix Achat + Transport + Assurance + Frais Divers
  × Taux de change CAD→GNF

Coût Total = Frais Canada en GNF + Dédouanement + Frais Divers Guinée
```

### Bénéfice Net (GNF)
```
Bénéfice Net = Prix de Revente - Coût Total
```

### Marge Bénéficiaire (%)
```
Marge = (Bénéfice Net / Coût Total) × 100%
```

### Répartition des Bénéfices
```
Part Associé = (Bénéfice Net × % Participation) / 100
```

## 🔧 Taux de Change

**Par défaut** : 1 CAD = 640 GNF

Le taux peut être modifié dans :
- `Constantes.TAUX_CHANGE_DEFAUT`
- Classe `ConvertisseurDevise`

## 📈 Tableau de Bord

Le tableau de bord affiche :
- Nombre total de voitures
- Nombre de voitures vendues
- Nombre de voitures en cours
- Coût total d'importation
- Revenu total
- Bénéfice total
- Marge moyenne

## 🗄️ Base de Données

SQLite avec les tables suivantes :

### Table VOITURE
```sql
CREATE TABLE VOITURE (
  id INTEGER PRIMARY KEY,
  marque VARCHAR(50),
  modele VARCHAR(50),
  annee INTEGER,
  immatriculation VARCHAR(20) UNIQUE,
  prixAchatCAD DECIMAL(10,2),
  transportCAD DECIMAL(10,2),
  assuranceCAD DECIMAL(10,2),
  dedouanementGNF DECIMAL(15,2),
  fraisDiversCAD DECIMAL(10,2),
  fraisDiversGNF DECIMAL(15,2),
  prixReventeGNF DECIMAL(15,2),
  dateImportation DATE,
  statut VARCHAR(20),
  associeId INTEGER
)
```

### Table ASSOCIE
```sql
CREATE TABLE ASSOCIE (
  id INTEGER PRIMARY KEY,
  nom VARCHAR(100),
  prenom VARCHAR(100),
  telephone VARCHAR(20),
  email VARCHAR(100),
  pourcentageParticipation DECIMAL(5,2)
)
```

### Table PAIEMENT
```sql
CREATE TABLE PAIEMENT (
  id INTEGER PRIMARY KEY,
  voitureId INTEGER,
  associeId INTEGER,
  montant DECIMAL(15,2),
  devise VARCHAR(3),
  datePaiement DATE,
  typePaiement VARCHAR(50),
  description VARCHAR(255)
)
```

### Table FRAIS
```sql
CREATE TABLE FRAIS (
  id INTEGER PRIMARY KEY,
  voitureId INTEGER,
  description VARCHAR(255),
  montant DECIMAL(10,2),
  devise VARCHAR(3),
  dateDepense DATE,
  categorie VARCHAR(50)
)
```

## 🔐 Validation des Données

La classe `Validateur` offre :
- Validation d'email
- Validation de numéro (entier/décimal)
- Validation de pourcentage (0-100)
- Validation d'année de voiture
- Validation de téléphone
- Validation d'immatriculation

## 📝 Conventions

- **Nommage** : camelCase pour les variables et méthodes, PascalCase pour les classes
- **Packages** : main.java.com.importation.{models,dao,ui}
- **Devises** : CAD (Canada Dollar), GNF (Franc Guinéen)
- **Dates** : Format ISO (YYYY-MM-DD)

## 🐛 Gestion des Erreurs

- Try-catch pour les opérations BD
- Affichage d'alertes utilisateur en cas d'erreur
- Logs en console pour le débogage

## 📞 Support et Améliorations Futures

Améliorations possibles :
- Export des données (CSV, PDF)
- Graphiques et statistiques avancées
- Historique des modifications
- Multi-utilisateurs avec authentification
- Sauvegarde automatique
- Gestion des images des voitures
- Intégration paiement en ligne

## 📄 Licence

Ce projet est fourni à titre d'exemple éducatif.

---

**Version** : 1.0.0  
**Date** : Février 2026  
**Auteur** : IBRAHIM BALDE
