package tn.enicarthage.plate_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicianScoreDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String specialite;
    private Integer totalPoints;
    private Double averageNote;
    private Integer rank;
    private String currentBadge;
    private String nextBadge;
    private Integer pointsToNextBadge;
    private List<PointTransactionDTO> transactions;
}