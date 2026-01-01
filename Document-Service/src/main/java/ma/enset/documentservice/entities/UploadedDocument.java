package ma.enset.documentservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "uploaded_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadedDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;
    private String filePath; // Path on disk

    private Long userId; // Candidate or Doctorant ID
    private String documentType; // CV, Diploma, etc.

    private LocalDateTime uploadDate;

    @Lob
    @Column(name = "content", columnDefinition = "VARBINARY(MAX)")
    private byte[] content; // Binary content if stored in DB directly
}
