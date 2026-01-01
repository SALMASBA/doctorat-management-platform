package ma.enset.documentservice.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.documentservice.dto.DocumentRequestDTO;
import ma.enset.documentservice.dto.ProcesVerbalDTO;
import ma.enset.documentservice.dto.SoutenanceAuthorizationDTO;
import ma.enset.documentservice.entities.GeneratedDocument;
import ma.enset.documentservice.entities.UploadedDocument;
import ma.enset.documentservice.services.DocumentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;

    // ==================== UPLOAD DE DOCUMENTS ====================

    /**
     * Upload d'un fichier (CV, diplôme, lettre de motivation, etc.)
     * POST /api/documents/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam("type") String type) {
        try {
            UploadedDocument document = documentService.uploadDocument(file, userId, type);
            log.info("Document uploaded successfully: {}", document.getId());
            return ResponseEntity.ok(document);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            log.error("Error uploading document", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'upload du fichier"));
        }
    }

    // ==================== GÉNÉRATION DE DOCUMENTS PDF ====================

    /**
     * Génère une attestation d'inscription
     * POST /api/documents/attestation-inscription
     */
    @PostMapping("/attestation-inscription")
    public ResponseEntity<?> generateAttestationInscription(@RequestBody DocumentRequestDTO request) {
        try {
            GeneratedDocument document = documentService.generateEnrollmentCertificate(request);
            log.info("Attestation d'inscription générée: {}", document.getReference());
            return ResponseEntity.status(HttpStatus.CREATED).body(document);
        } catch (IOException e) {
            log.error("Error generating attestation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la génération de l'attestation"));
        }
    }

    /**
     * Génère une autorisation de soutenance
     * POST /api/documents/autorisation-soutenance
     */
    @PostMapping("/autorisation-soutenance")
    public ResponseEntity<?> generateAutorisationSoutenance(@RequestBody SoutenanceAuthorizationDTO request) {
        try {
            GeneratedDocument document = documentService.generateSoutenanceAuthorization(request);
            log.info("Autorisation de soutenance générée: {}", document.getReference());
            return ResponseEntity.status(HttpStatus.CREATED).body(document);
        } catch (IOException e) {
            log.error("Error generating authorization", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la génération de l'autorisation"));
        }
    }

    /**
     * Génère un procès-verbal de soutenance
     * POST /api/documents/proces-verbal
     */
    @PostMapping("/proces-verbal")
    public ResponseEntity<?> generateProcesVerbal(@RequestBody ProcesVerbalDTO request) {
        try {
            GeneratedDocument document = documentService.generateProcesVerbal(request);
            log.info("Procès-verbal généré: {}", document.getReference());
            return ResponseEntity.status(HttpStatus.CREATED).body(document);
        } catch (IOException e) {
            log.error("Error generating PV", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la génération du procès-verbal"));
        }
    }

    /**
     * Génère une demande manuscrite
     * POST /api/documents/handwritten-request
     */
    @PostMapping("/handwritten-request")
    public ResponseEntity<?> generateHandwrittenRequest(@RequestBody DocumentRequestDTO request) {
        try {
            GeneratedDocument document = documentService.generateHandwrittenRequest(request);
            log.info("Demande manuscrite générée: {}", document.getReference());
            return ResponseEntity.status(HttpStatus.CREATED).body(document);
        } catch (IOException e) {
            log.error("Error generating handwritten request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la génération de la demande manuscrite"));
        }
    }

    /**
     * Génère les copies des attestations de formation
     * POST /api/documents/training-certificates
     */
    @PostMapping("/training-certificates")
    public ResponseEntity<?> generateTrainingCertificates(@RequestBody DocumentRequestDTO request) {
        try {
            GeneratedDocument document = documentService.generateTrainingCertificates(request);
            log.info("Attestations de formation générées: {}", document.getReference());
            return ResponseEntity.status(HttpStatus.CREATED).body(document);
        } catch (IOException e) {
            log.error("Error generating training certificates", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la génération des attestations"));
        }
    }

    // ==================== CONSULTATION DES DOCUMENTS ====================

    /**
     * Liste tous les documents uploadés par un utilisateur
     * GET /api/documents/user/{userId}/uploads
     */
    @GetMapping("/user/{userId}/uploads")
    public ResponseEntity<List<UploadedDocument>> getUserUploads(@PathVariable Long userId) {
        return ResponseEntity.ok(documentService.getUserUploads(userId));
    }

    /**
     * Liste tous les documents générés pour un utilisateur
     * GET /api/documents/user/{userId}/generated
     */
    @GetMapping("/user/{userId}/generated")
    public ResponseEntity<List<GeneratedDocument>> getUserGeneratedDocuments(@PathVariable Long userId) {
        return ResponseEntity.ok(documentService.getUserGeneratedDocuments(userId));
    }

    /**
     * Recherche un document par sa référence
     * GET /api/documents/reference/{reference}
     */
    @GetMapping("/reference/{reference}")
    public ResponseEntity<?> getDocumentByReference(@PathVariable String reference) {
        return documentService.getDocumentByReference(reference)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== PRÉVISUALISATION ET TÉLÉCHARGEMENT ====================

    /**
     * Prévisualisation d'un document uploadé (affiche dans le navigateur)
     * GET /api/documents/preview/upload/{id}
     */
    @GetMapping("/preview/upload/{id}")
    public ResponseEntity<byte[]> previewUploadedDocument(@PathVariable Long id) {
        return documentService.getUploadedDocument(id)
                .map(doc -> {
                    MediaType mediaType = getMediaType(doc.getFileType());
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getFileName() + "\"")
                            .contentType(mediaType)
                            .body(doc.getContent());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Prévisualisation d'un document généré (affiche dans le navigateur)
     * GET /api/documents/preview/generated/{id}
     */
    @GetMapping("/preview/generated/{id}")
    public ResponseEntity<byte[]> previewGeneratedDocument(@PathVariable Long id) {
        return documentService.getGeneratedDocument(id)
                .map(doc -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getReference() + ".pdf\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(doc.getContent()))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Téléchargement d'un document uploadé
     * GET /api/documents/download/upload/{id}
     */
    @GetMapping("/download/upload/{id}")
    public ResponseEntity<byte[]> downloadUploadedDocument(@PathVariable Long id) {
        return documentService.getUploadedDocument(id)
                .map(doc -> {
                    MediaType mediaType = getMediaType(doc.getFileType());
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    "attachment; filename=\"" + doc.getFileName() + "\"")
                            .contentType(mediaType)
                            .body(doc.getContent());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Téléchargement d'un document généré
     * GET /api/documents/download/generated/{id}
     */
    @GetMapping("/download/generated/{id}")
    public ResponseEntity<byte[]> downloadGeneratedDocument(@PathVariable Long id) {
        return documentService.getGeneratedDocument(id)
                .map(doc -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + doc.getReference() + ".pdf\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(doc.getContent()))
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== SUPPRESSION ====================

    /**
     * Supprime un document uploadé
     * DELETE /api/documents/upload/{id}
     */
    @DeleteMapping("/upload/{id}")
    public ResponseEntity<?> deleteUploadedDocument(@PathVariable Long id) {
        try {
            documentService.deleteUploadedDocument(id);
            return ResponseEntity.ok(Map.of("message", "Document supprimé avec succès"));
        } catch (IOException e) {
            log.error("Error deleting uploaded document", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la suppression du document"));
        }
    }

    /**
     * Supprime un document généré
     * DELETE /api/documents/generated/{id}
     */
    @DeleteMapping("/generated/{id}")
    public ResponseEntity<?> deleteGeneratedDocument(@PathVariable Long id) {
        try {
            documentService.deleteGeneratedDocument(id);
            return ResponseEntity.ok(Map.of("message", "Document supprimé avec succès"));
        } catch (IOException e) {
            log.error("Error deleting generated document", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la suppression du document"));
        }
    }

    // ==================== HEALTH CHECK ====================

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "document-service",
                "message", "Service de gestion des documents opérationnel"));
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    private MediaType getMediaType(String contentType) {
        if (contentType == null) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        if ("application/pdf".equals(contentType)) {
            return MediaType.APPLICATION_PDF;
        } else if ("image/jpeg".equals(contentType) || "image/jpg".equals(contentType)) {
            return MediaType.IMAGE_JPEG;
        } else if ("image/png".equals(contentType)) {
            return MediaType.IMAGE_PNG;
        } else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
