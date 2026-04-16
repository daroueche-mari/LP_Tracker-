Database.sql contient les commandes nécessaires pour la creation de la base de donnée.

***

# LP Tracker - Système de Gestion d'Étudiants

**LP Tracker** est une application de bureau moderne développée en **JavaFX**. Elle permet de gérer un parc d'étudiants de manière intuitive avec une persistance des données sur une base de données relationnelle. Le projet met l'accent sur la sécurité des accès et l'interopérabilité des données.

---

## Fonctionnalités Principales

### Gestion des Étudiants (CRUD)
* **Visualisation** : Liste complète avec pagination dynamique (optimisée pour le plein écran).
* **Édition** : Ajout, modification et suppression rapide d'étudiants.
* **Filtres** : Recherche par nom/prénom et filtrage par âge.

### Import & Export Multi-formats
L'application propose des outils avancés pour la manipulation de données :
* **Formats supportés** : CSV, JSON, XML, et HTML.
* **Génération de rapports** : Exportation de la liste des étudiants pour un usage externe.

### Sécurité & Authentification
* **Contrôle d'accès** : Écran de connexion sécurisé au démarrage.
* **Hachage Salé (Salt)** : Les mots de passe sont protégés par un sel cryptographique unique, empêchant la lecture en clair des accès en base de données.

---

## Architecture de la Base de Données

Le projet repose sur deux tables principales pour séparer la logique métier de la sécurité.

### 1. Table `student` (Données Métiers)
Stocke les informations académiques des élèves.
| Colonne | Type | Description |
| :--- | :--- | :--- |
| **id** | SERIAL | Clé primaire auto-incrémentée (int4) |
| **first_name** | VARCHAR | Prénom de l'étudiant |
| **last_name** | VARCHAR | Nom de famille |
| **age** | INT | Âge de l'étudiant |
| **grade** | DECIMAL | Note ou moyenne générale |

### 2. Table `users` (Sécurité des accès)
Gère les comptes utilisateurs de l'application.
| Colonne | Type | Description |
| :--- | :--- | :--- |
| **id** | SERIAL | Identifiant unique de l'utilisateur |
| **username** | VARCHAR | Nom d'utilisateur (Unique) |
| **password** | VARCHAR | Mot de passe haché |
| **salt** | VARCHAR | Sel unique pour sécuriser le hachage |
| **avatar_url**| VARCHAR | Lien optionnel vers l'image de profil |

---

## 🛠️ Stack Technique

* **Interface** : JavaFX 25 (MVC Pattern)
* **Design** : CSS personnalisé (Thème "Moderne Connecté") & Scene Builder
* **Base de données** : PostgreSQL
* **Gestionnaire de dépendances** : Maven
* **Sécurité** : Hachage de mot de passe avec Salt

---

## Design & Ergonomie

L'interface a été conçue pour être à la fois sobre et fonctionnelle :
* **Logo** : Design sur mesure intégrant les initiales "LP" et une flèche de progression.
* **Code Couleur** : Utilisation du bleu nuit (`#2c3e50`) pour la sidebar et de couleurs sémantiques pour les actions (Vert = Succès, Rouge = Danger).
* **Responsive** : La `TableView` et sa pagination s'adaptent automatiquement à la taille de la fenêtre (Plein écran supporté).

---

## Installation

1. Cloner le repository.
2. Importer le schéma SQL fourni dans votre base de données.
3. Configurer les accès à la base de données dans la classe `DatabaseConnection`.
4. Lancer l'application via Maven : `mvn javafx:run`.

---
*Projet réalisé en groupe.*
