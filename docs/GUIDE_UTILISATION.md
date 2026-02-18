# GUIDE D'UTILISATION - Gestion Importation Voitures

## 🚀 Démarrage Rapide

### Sur Windows
1. Ouvrez un terminal (cmd ou PowerShell)
2. Naviguez vers le répertoire du projet
3. Exécutez : `run.bat`

### Sur Linux/Mac
1. Ouvrez un terminal
2. Naviguez vers le répertoire du projet
3. Rendez le script exécutable : `chmod +x run.sh`
4. Exécutez : `./run.sh`

### Avec Ant
```bash
ant run
```

## 📊 Interface Principale

L'application est organisée en 4 sections principales :

### 1️⃣ Tableau de Bord (Dashboard)
- Vue d'ensemble des statistiques
- Nombre total de voitures
- Nombre de voitures vendues / en cours
- Coût total d'importation
- Revenu total et bénéfice net
- Marge bénéficiaire moyenne

### 2️⃣ Gestion des Voitures
Fonctions disponibles :
- **Ajouter une voiture** : Bouton "+ Nouvelle Voiture"
- **Modifier** : Sélectionner une voiture et cliquer "Modifier"
- **Supprimer** : Sélectionner une voiture et cliquer "Supprimer"
- **Filtrer** : Par statut (EN_COURS, VENDUE, SUSPENDUE)
- **Rafraîchir** : Recharger les données

### 3️⃣ Gestion des Associés
Fonctions disponibles :
- **Ajouter un associé** : Bouton "+ Nouvel Associé"
- **Modifier** : Sélectionner un associé et cliquer "Modifier"
- **Supprimer** : Sélectionner un associé et cliquer "Supprimer"
- **Voir les statistiques** : Solde, contributions, revenus

### 4️⃣ Taux de Change
- Modifier le taux CAD → GNF
- Taux par défaut : 1 CAD = 1000 GNF
- S'applique à tous les calculs

## 📝 Ajouter une Voiture

1. Cliquez sur **"+ Nouvelle Voiture"**
2. Remplissez les champs :

### Informations Générales
- **Marque** : ex. "Toyota"
- **Modèle** : ex. "Corolla"
- **Année** : ex. "2020"
- **Immatriculation** : ex. "GN-2024-123"
- **Associé** : Sélectionnez le responsable
- **Statut** : EN_COURS / VENDUE / SUSPENDUE
- **Date d'importation** : Date d'arrivée

### Frais Canada (en CAD)
- **Prix d'achat** : ex. "15000"
- **Transport** : ex. "2000"
- **Assurance** : ex. "300"
- **Frais divers** : ex. "500"

### Frais Guinée (en GNF)
- **Dédouanement** : ex. "500000"
- **Frais divers** : ex. "200000"

### Revente
- **Prix de revente (GNF)** : ex. "35000000"

### Calculs Automatiques
L'application affiche automatiquement :
- ✅ **Coût total** : Tous les frais convertis et additionnés en GNF
- ✅ **Bénéfice net** : Prix revente - Coût total
- ✅ **Marge** : Pourcentage de rentabilité

3. Vérifiez les calculs
4. Cliquez **"Enregistrer"**

## 👥 Ajouter un Associé

1. Allez dans l'onglet **"Associés"**
2. Cliquez **"+ Nouvel Associé"**
3. Remplissez les données :
   - **Prénom** : ex. "Jean"
   - **Nom** : ex. "Diallo"
   - **Téléphone** : ex. "+224 600 123 456"
   - **Email** : ex. "jean.diallo@email.com"
   - **% Participation** : ex. "33.33"

⚠️ **Important** : La somme des pourcentages doit faire 100%

4. Cliquez **"Enregistrer"**

## 💰 Calculs et Formules

### Coût Total d'Importation
```
Frais Canada (en GNF) = (Prix achat + Transport + Assurance + Frais divers Canada) × Taux
Coût Total = Frais Canada (GNF) + Dédouanement + Frais divers Guinée
```

### Exemple Concret
```
Prix achat Canada        : 15 000 CAD  → 15 000 000 GNF
Transport               :  2 000 CAD  →  2 000 000 GNF
Assurance               :    300 CAD  →    300 000 GNF
Frais divers Canada     :    500 CAD  →    500 000 GNF
─────────────────────────────────────────────────
Frais Canada (GNF)      :             → 17 800 000 GNF
+ Dédouanement Guinée   :               500 000 GNF
+ Frais divers Guinée   :               200 000 GNF
─────────────────────────────────────────────────
COÛT TOTAL              :            = 18 500 000 GNF

Prix de revente         :            = 35 000 000 GNF
─ Coût total            :            = 18 500 000 GNF
─────────────────────────────────────────────────
BÉNÉFICE NET            :            = 16 500 000 GNF

Marge                   : 16 500 000 / 18 500 000 × 100 = 89,19%
```

## 📋 Tableau des Voitures

Le tableau affiche :
- **ID** : Numéro unique
- **Marque/Modèle** : Identification du véhicule
- **Année** : Année de fabrication
- **Immatriculation** : Numéro d'immatriculation
- **Prix Achat (CAD)** : Prix d'achat au Canada
- **Coût Total (GNF)** : Total d'importation en Guinée
- **Prix Revente (GNF)** : Prix de vente prévu
- **Bénéfice (GNF)** : Profit net
- **Statut** : État actuel
- **Associé** : Responsable

## 👨‍💼 Tableau des Associés

Le tableau affiche pour chaque associé :
- **Prénom/Nom** : Identité
- **Téléphone/Email** : Contacts
- **% Participation** : Part dans les bénéfices
- **Total Contributions** : Somme des contributions en GNF
- **Total Revenus** : Somme des revenus en GNF
- **Solde Net** : Bilan (revenus - contributions)

## 🔄 Répartition des Bénéfices

Quand une voiture est vendue :

1. Calculez le bénéfice net
2. Le système répartit automatiquement aux associés
3. Chacun reçoit : Bénéfice × (Son % / 100)

**Exemple** : Bénéfice de 16 500 000 GNF avec 3 associés à 33,33% chacun
- Associé 1 : 16 500 000 × 0,3333 = 5 500 000 GNF
- Associé 2 : 16 500 000 × 0,3333 = 5 500 000 GNF
- Associé 3 : 16 500 000 × 0,3334 = 5 500 000 GNF

## ⚙️ Modification du Taux de Change

1. Allez dans **"Taux de Change"**
2. Saisissez le nouveau taux (ex. "950" pour 1 CAD = 950 GNF)
3. Cliquez **"Appliquer"**
4. Tous les calculs se mettent à jour automatiquement

## 🎨 Personnalisation

### Couleurs et Thème
Le fichier `style.css` contrôle l'apparence. Vous pouvez modifier :
- Couleurs principales
- Polices
- Tailles
- Espacements

### Devises
Pour changer les devises :
1. Modifiez `Constantes.java`
2. Mettez à jour `ConvertisseurDevise.java`
3. Recompilez

## 💾 Données

### Sauvegarde Automatique
Les données sont sauvegardées automatiquement dans SQLite lors de chaque opération.

### Base de Données
Emplacement : `database/import_voitures.db`

### Sauvegarder les Données
1. Fermez l'application
2. Copiez le fichier `database/import_voitures.db`
3. Conservez-le en lieu sûr

### Restaurer les Données
1. Fermez l'application
2. Remplacez `database/import_voitures.db` par votre sauvegarde
3. Redémarrez

## ⚠️ Limitations Connues

- Maximum 3 associés (modifiable dans le code)
- Les calculs sont basés sur le taux de change actuel
- Pas d'annulation d'une suppression (soyez prudent)
- Pas d'export de données (version future)

## 🆘 Dépannage

### L'application ne démarre pas
```
Erreur possible : Java ou JavaFX non installés
Solution : Installez Java 21+ et JavaFX SDK 21
```

### Erreur de base de données
```
Erreur possible : Permissions insuffisantes
Solution : Vérifiez les permissions du dossier
```

### Les calculs sont incorrects
```
Erreur possible : Taux de change incorrect
Solution : Vérifiez le taux dans "Taux de Change"
```

### Impossible d'ajouter une voiture
```
Erreur possible : Champ obligatoire manquant
Solution : Vérifiez tous les champs requis
```

## 📞 Support

Pour toute question ou problème :
1. Consultez ce guide
2. Vérifiez le README.md
3. Consultez le code source commenté

---

**Bon usage !** 🎉
