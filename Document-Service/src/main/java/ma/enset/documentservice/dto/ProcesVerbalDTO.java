package ma.enset.documentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO pour la génération du Procès-Verbal de soutenance
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcesVerbalDTO {
    private String firstName;
    private String lastName;
    private String cin;
    private String cne;
    private String thesisTitle;
    private String laboratory;
    private String formation;
    private String directorName;
    private LocalDate soutenanceDate;
    private String soutenanceTime;
    private String soutenanceLocation;
    private Long userId;

    // Résultat de la soutenance
    private String mention; // Très Honorable avec Félicitations, Très Honorable, Honorable
    private String decision; // ADMIS, AJOURNE

    // Jury members avec signatures
    private List<JuryMemberPVDTO> juryMembers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class JuryMemberPVDTO {
        private String name;
        private String role;
        private String institution;
        private String grade;
        private boolean hasApproved;
    }
}
