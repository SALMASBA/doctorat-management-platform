package ma.enset.documentservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.documentservice.dto.DocumentRequestDTO;
import ma.enset.documentservice.dto.ProcesVerbalDTO;
import ma.enset.documentservice.dto.SoutenanceAuthorizationDTO;
import ma.enset.documentservice.entities.GeneratedDocument;
import ma.enset.documentservice.entities.UploadedDocument;
import ma.enset.documentservice.repositories.GeneratedDocumentRepository;
import ma.enset.documentservice.repositories.UploadedDocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final UploadedDocumentRepository uploadedDocumentRepository;
    private final GeneratedDocumentRepository generatedDocumentRepository;
    private final PdfGeneratorService pdfGeneratorService;

    @Value("${app.documents.storage-path:./uploads/documents}")
    private String storagePath;

    // Types de documents acceptés pour l'upload
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "application/pdf",
            "image/jpeg",
            "image/jpg",
            "image/png");

    // ==================== UPLOAD DE DOCUMENTS ====================

    /**
     * Upload d'un document (CV, diplôme, lettre de motivation, etc.)
     */
    public UploadedDocument uploadDocument(MultipartFile file, Long userId, String documentType) throws IOException {
        validateFile(file);

        Path root = Paths.get(storagePath, "uploads");
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }

        String fileName = UUID.randomUUID() + "_" + sanitizeFileName(file.getOriginalFilename());
        Path targetPath = root.resolve(fileName);
        Files.copy(file.getInputStream(), targetPath);

        UploadedDocument document = UploadedDocument.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .filePath(targetPath.toString())
                .userId(userId)
                .documentType(documentType)
                .uploadDate(LocalDateTime.now())
                .content(file.getBytes())
                .build();

        log.info("Document uploaded: {} for user {}", file.getOriginalFilename(), userId);
        return uploadedDocumentRepository.save(document);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide.");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Type de fichier non autorisé. Formats acceptés: PDF, JPG, PNG");
        }
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null)
            return "document";
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    // ==================== GÉNÉRATION D'ATTESTATION D'INSCRIPTION
    // ====================

    /**
     * Génère une attestation d'inscription pour un doctorant
     */
    public GeneratedDocument generateEnrollmentCertificate(DocumentRequestDTO request) throws IOException {
        byte[] pdfContent = pdfGeneratorService.generateEnrollmentCertificate(request);
        return saveGeneratedDocument(pdfContent, "Attestation d'Inscription", "ATTESTATION_INSCRIPTION",
                request.getUserId());
    }

    // ==================== GÉNÉRATION D'AUTORISATION DE SOUTENANCE
    // ====================

    /**
     * Génère une autorisation de soutenance
     */
    public GeneratedDocument generateSoutenanceAuthorization(SoutenanceAuthorizationDTO request) throws IOException {
        byte[] pdfContent = pdfGeneratorService.generateSoutenanceAuthorization(request);
        return saveGeneratedDocument(pdfContent, "Autorisation de Soutenance", "AUTORISATION_SOUTENANCE",
                request.getUserId());
    }

    // ==================== GÉNÉRATION DU PROCÈS-VERBAL ====================

    /**
     * Génère un procès-verbal de soutenance
     */
    public GeneratedDocument generateProcesVerbal(ProcesVerbalDTO request) throws IOException {
        byte[] pdfContent = pdfGeneratorService.generateProcesVerbal(request);
        return saveGeneratedDocument(pdfContent, "Procès-Verbal de Soutenance", "PROCES_VERBAL", request.getUserId());
    }

    /**
     * Génère une demande manuscrite
     */
    public GeneratedDocument generateHandwrittenRequest(DocumentRequestDTO request) throws IOException {
        byte[] pdfContent = pdfGeneratorService.generateHandwrittenRequest(request);
        return saveGeneratedDocument(pdfContent, "Demande Manuscrite", "DEMANDE_MANUSCRITE", request.getUserId());
    }

    /**
     * Génère les copies des attestations de formation
     */
    public GeneratedDocument generateTrainingCertificates(DocumentRequestDTO request) throws IOException {
        byte[] pdfContent = pdfGeneratorService.generateTrainingCertificateCopies(request);
        return saveGeneratedDocument(pdfContent, "Attestations de Formation", "ATTESTATIONS_FORMATION",
                request.getUserId());
    }

    // ==================== MÉTHODE COMMUNE POUR SAUVEGARDER UN DOCUMENT GÉNÉRÉ
    // ====================

    private GeneratedDocument saveGeneratedDocument(byte[] pdfContent, String title, String documentType, Long userId)
            throws IOException {
        Path root = Paths.get(storagePath, "generated");
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }

        String reference = generateReference(documentType);
        String fileName = documentType.toLowerCase() + "_" + userId + "_" + UUID.randomUUID() + ".pdf";
        Path targetPath = root.resolve(fileName);
        Files.write(targetPath, pdfContent);

        GeneratedDocument document = GeneratedDocument.builder()
                .title(title)
                .reference(reference)
                .documentType(documentType)
                .userId(userId)
                .generationDate(LocalDateTime.now())
                .filePath(targetPath.toString())
                .content(pdfContent)
                .build();

        log.info("Generated document: {} (Ref: {}) for user {}", title, reference, userId);
        return generatedDocumentRepository.save(document);
    }

    private String generateReference(String documentType) {
        String prefix;
        switch (documentType) {
            case "ATTESTATION_INSCRIPTION":
                prefix = "ATT";
                break;
            case "AUTORISATION_SOUTENANCE":
                prefix = "AUT";
                break;
            case "PROCES_VERBAL":
                prefix = "PV";
                break;
            case "DEMANDE_MANUSCRITE":
                prefix = "DEM";
                break;
            case "ATTESTATIONS_FORMATION":
                prefix = "FOR";
                break;
            default:
                prefix = "DOC";
        }
        return prefix + "-" + LocalDateTime.now().getYear() + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // ==================== CONSULTATION DES DOCUMENTS ====================

    /**
     * Récupère tous les documents uploadés par un utilisateur
     */
    public List<UploadedDocument> getUserUploads(Long userId) {
        return uploadedDocumentRepository.findByUserId(userId);
    }

    /**
     * Récupère tous les documents générés pour un utilisateur
     */
    public List<GeneratedDocument> getUserGeneratedDocuments(Long userId) {
        return generatedDocumentRepository.findByUserId(userId);
    }

    /**
     * Récupère un document généré par sa référence
     */
    public Optional<GeneratedDocument> getDocumentByReference(String reference) {
        return generatedDocumentRepository.findByReference(reference);
    }

    /**
     * Récupère le contenu binaire d'un document uploadé
     */
    public byte[] getUploadedDocumentContent(Long id) {
        return uploadedDocumentRepository.findById(id)
                .map(UploadedDocument::getContent)
                .orElse(null);
    }

    /**
     * Récupère le contenu binaire d'un document généré
     */
    public byte[] getGeneratedDocumentContent(Long id) {
        return generatedDocumentRepository.findById(id)
                .map(GeneratedDocument::getContent)
                .orElse(null);
    }

    /**
     * Récupère un document uploadé par son ID
     */
    public Optional<UploadedDocument> getUploadedDocument(Long id) {
        return uploadedDocumentRepository.findById(id);
    }

    /**
     * Récupère un document généré par son ID
     */
    public Optional<GeneratedDocument> getGeneratedDocument(Long id) {
        return generatedDocumentRepository.findById(id);
    }

    // ==================== SUPPRESSION ====================

    /**
     * Supprime un document uploadé
     */
    public void deleteUploadedDocument(Long id) throws IOException {
        Optional<UploadedDocument> doc = uploadedDocumentRepository.findById(id);
        if (doc.isPresent()) {
            Path filePath = Paths.get(doc.get().getFilePath());
            Files.deleteIfExists(filePath);
            uploadedDocumentRepository.deleteById(id);
            log.info("Deleted uploaded document with id: {}", id);
        }
    }

    /**
     * Supprime un document généré
     */
    public void deleteGeneratedDocument(Long id) throws IOException {
        Optional<GeneratedDocument> doc = generatedDocumentRepository.findById(id);
        if (doc.isPresent()) {
            Path filePath = Paths.get(doc.get().getFilePath());
            Files.deleteIfExists(filePath);
            generatedDocumentRepository.deleteById(id);
            log.info("Deleted generated document with id: {}", id);
        }
    }
}
