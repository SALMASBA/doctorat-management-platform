package ma.enset.documentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRequestDTO {
    private String firstName;
    private String lastName;
    private String cin;
    private String cne;
    private String formation;
    private String university;
    private String laboratory;
    private String thesisTitle;
    private String directorName;
    private Long userId;
}
