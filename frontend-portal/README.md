# Portail Doctoral - Frontend

Application Angular pour le portail de suivi du doctorat.
Interface premium respectant la charte graphique de l'université.

## Fonctionnalités Document Service

*   **Mes Dépôts** : Liste des fichiers uploadés.
*   **Documents Officiels** : Liste des PDF générés (Attestations, PV).
*   **Dépôt** : Upload de fichiers (CV, Diplômes).
*   **Génération** : Demande d'attestation d'inscription.

## Installation et Démarrage

1.  Installer les dépendances :
    ```bash
    npm install
    ```

2.  Démarrer le serveur de développement :
    ```bash
    ng serve
    ```
    L'application sera accessible sur `http://localhost:4200`.
    Le proxy est configuré pour rediriger les appels `/api` vers `http://localhost:8083` (Document Service).

## Configuration

*   **Proxy** : Voir `proxy.conf.json`.
*   **Styles** : Voir `src/styles.scss` pour le thème (Couleurs, Typographie).
*   **Service** : `src/app/documents/document.service.ts` gère les appels API.

> Note : Pour la démonstration, l'ID utilisateur est fixé à `1`.
