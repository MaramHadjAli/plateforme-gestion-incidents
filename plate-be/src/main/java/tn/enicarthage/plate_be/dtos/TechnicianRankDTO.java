package tn.enicarthage.plate_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianRankDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private Integer totalPoints;
    private Double averageNote;
    private Integer rank;
    private String badge;
}