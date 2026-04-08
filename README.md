Feature Statut :
Authentification,✅
Import/Export CSV,✅
Rapport HTML (Resultats),✅
"Statistiques (AVG, COUNT, GROUP BY)",✅
Recherche Avancée,✅
Tri des colonnes,✅
Sauvegarde automatique,✅

1. Sécurité & Accès
Système d'Authentification : Création d'une table users en base de données et d'un écran de Login sécurisé au démarrage. Seuls les utilisateurs enregistrés peuvent accéder au tracker.

Déconnexion : Ajout d'une fonction pour quitter la session proprement et revenir à l'accueil.

2. Statistiques & SQL Avancé
Analyse de données : Tu n'affiches plus seulement des lignes, tu les analyses. On a implémenté des requêtes utilisant les fonctions d'agrégation :

AVG() pour la moyenne générale.

COUNT() pour le nombre total d'élèves.

GROUP BY age pour voir la répartition démographique de la classe.

Mise à jour en temps réel : Les statistiques se recalculent automatiquement à chaque ajout, suppression ou filtrage.

3. Recherche Avancée (Multi-critères)
Filtres dynamiques : Tu as maintenant une barre de recherche capable de filtrer simultanément par :

Nom (via la clause ILIKE pour être insensible à la casse).

Note minimale (pour trouver les élèves en difficulté ou en réussite).

Âge (via une ComboBox).

Requête Dynamique : Le code Java construit intelligemment la requête SQL WHERE 1=1 AND ... selon ce que l'utilisateur remplit.

4. Gestion des Fichiers (Import/Export)
Import CSV : Capacité de charger massivement des élèves depuis un fichier externe via un BufferedReader.

Export CSV : Sauvegarde de la liste actuelle dans un format compatible Excel.

Rapport HTML/PDF : Génération d'un fichier HTML stylisé (avec tableau et couleurs) pour présenter les résultats, imprimable facilement en PDF.

5. Ergonomie & UI (JavaFX)
Interface organisée : Trois lignes de boutons bien distinctes (Gestion / Données / Rapports).

Tri natif : Les colonnes sont triables en un clic grâce à la configuration du PropertyValueFactory.

Sauvegarde Auto : Puisque tout est lié à PostgreSQL, chaque action est persistante immédiatement.
