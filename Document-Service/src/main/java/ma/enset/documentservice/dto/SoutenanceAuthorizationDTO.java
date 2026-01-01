package ma.enset.documentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO pour la génération de l'autorisation de soutenance
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SoutenanceAuthorizationDTO {
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

    // Jury members
    private List<JuryMemberDTO> juryMembers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class JuryMemberDTO {
        private String name;
        private String role; // PRESIDENT, RAPPORTEUR, EXAMINATEUR, DIRECTEUR
        private String institution;
        private String grade; // Professeur, PH, PA
    }
}
