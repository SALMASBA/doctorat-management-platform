# Document Service

Ce microservice gère le cycle de vie des documents administratifs et académiques pour le portail de doctorat. Il fournit des fonctionnalités clés telles que l'upload de fichiers, la génération automatique de documents PDF (attestations, autorisations, PV), et la gestion du stockage sécurisé.

## Fonctionnalités Principales

1.  **Gestion des Dépôts (Uploads)**
    *   Prise en charge des CV, diplômes, relevés de notes, etc.
    *   Stockage sécurisé sur le disque.
    *   API pour télécharger et prévisualiser les fichiers.

2.  **Génération de PDF (Moteur iText/OpenPDF)**
    *   **Attestation d'Inscription** : Générée automatiquement pour les doctorants inscrits.
    *   **Autorisation de Soutenance** : Créée lors de la validation du dossier de soutenance.
    *   **Procès-Verbal (PV) de Soutenance** : Document officiel pour le jury.

3.  **Consultation et Suivi**
    *   Historique des documents par utilisateur.
    *   Recherche par référence unique.
    *   Prévisualisation en ligne.

## Architecture Technique

*   **Framework** : Spring Boot 3.4.1
*   **Base de Données** : SQL Server (via Spring Data JPA)
*   **Génération PDF** : OpenPDF (Fork libre de iText)
*   **Service Discovery** : Eureka Client
*   **Stockage** : Système de fichiers local (configurable) + Métadonnées en DB

## Configuration

Le service est configuré via `application.properties`. Les points clés sont :

```properties
server.port=8083
spring.application.name=document-service

# Chemin de stockage des fichiers
app.documents.storage-path=./uploads/documents

# Base de données
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=document_db;encrypt=true;trustServerCertificate=true
```

## API Endpoints

### Uploads
*   `POST /api/documents/upload` : Upload d'un fichier (params: `file`, `userId`, `type`)
*   `GET /api/documents/user/{userId}/uploads` : Liste des uploads d'un utilisateur
*   `GET /api/documents/preview/upload/{id}` : Aperçu d'un fichier uploadé
*   `GET /api/documents/download/upload/{id}` : Téléchargement d'un fichier uploadé

### Génération de Documents
*   `POST /api/documents/attestation-inscription` : Génère une attestation d'inscription (Body: `DocumentRequestDTO`)
*   `POST /api/documents/autorisation-soutenance` : Génère une autorisation de soutenance (Body: `SoutenanceAuthorizationDTO`)
*   `POST /api/documents/proces-verbal` : Génère un PV de soutenance (Body: `ProcesVerbalDTO`)
*   `GET /api/documents/user/{userId}/generated` : Liste des documents générés
*   `GET /api/documents/preview/generated/{id}` : Aperçu d'un PDF généré

## Démarrage

1.  Assurez-vous que SQL Server est lancé et que la base `document_db` est créée.
2.  Assurez-vous que le Discovery Service (Eureka) est lancé sur le port 8761.
3.  Lancez le service :
    ```bash
    mvn spring-boot:run
    ```
