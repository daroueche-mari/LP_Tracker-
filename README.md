# 🎓 LP Tracker - Système de Gestion d'Étudiants

**LP Tracker** est une application de bureau moderne développée en **JavaFX**. Elle permet de gérer efficacement une base de données d'étudiants, d'analyser leurs performances et d'exporter les données sous divers formats professionnels.

## 🚀 Fonctionnalités Clés

### 📝 Gestion CRUD Complète
* **Ajout, Modification et Suppression** d'étudiants (Prénom, Nom, Âge, Note).
* **Interface interactive** avec mise à jour en temps réel de la liste.

### 🔍 Recherche et Filtrage Avancés
* Recherche dynamique par **nom** ou **prénom**.
* Filtrage par **tranches d'âge** via un menu déroulant.
* Bouton de réinitialisation rapide des filtres.

### 📊 Analyses et Statistiques
* Calcul automatique de la **moyenne générale** de la classe.
* Suivi du nombre total d'étudiants inscrits.

### 📥 Import / Export Multiformat
* **CSV** : Pour une compatibilité Excel parfaite.
* **JSON & XML** : Pour l'interopérabilité avec d'autres systèmes.
* **HTML / PDF** : Génération de rapports visuels pour l'impression.

### 🔐 Sécurité et Authentification
* Système complet de **Login** et **Inscription**.
* Validation des données et gestion des erreurs de connexion.

---

## 🛠️ Stack Technique

* **Langage** : Java 21
* **Interface Graphique** : JavaFX (FXML)
* **Style** : CSS personnalisé pour un rendu moderne (UI/UX).
* **Architecture** : Design Pattern **DAO** (Data Access Object) pour séparer la logique métier de la base de données.
* **Gestion de dépendances** : Maven.

---

## 🎨 Aperçu de l'Interface

L'application utilise un design en "Split Screen" pour l'authentification et un "Dashboard" avec barre latérale pour la gestion principale.

* **Couleurs de l'UI** : Anthracite (`#2c3e50`), Orange (`#e67e22`) et Vert émeraude (`#27ae60`).
* **Interactivité** : Animations au survol (Hover effects) gérées intégralement en CSS.

---

## ⚙️ Installation

1.  Cloner le repository :
    ```bash
    git clone https://github.com/ton-pseudo/laplateforme_tracker.git
    ```
2.  Importer le projet dans votre IDE (IntelliJ, Eclipse ou VS Code).
3.  Lancer la classe `mvn javafx:run`.
