package tn.enicarthage.plate_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TechnicienRankingDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private Integer totalPoints;
    private Double averageNote;
    private Integer rank;
    private String badge;
}
