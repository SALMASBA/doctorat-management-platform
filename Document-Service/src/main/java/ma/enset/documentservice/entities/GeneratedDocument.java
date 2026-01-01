package ma.enset.documentservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "generated_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneratedDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String reference; // Unique reference
    private String documentType; // ATTESTATION, PV, etc.

    private Long userId;
    private LocalDateTime generationDate;

    private String filePath;

    @Lob
    @Column(name = "content", columnDefinition = "VARBINARY(MAX)")
    private byte[] content;
}
