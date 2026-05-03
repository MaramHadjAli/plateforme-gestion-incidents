package tn.enicarthage.plate_be.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.enicarthage.plate_be.entities.ROLE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private ROLE role;
    private String telephone;
    private String avatarUrl;
    private boolean twoFactorEnabled;
    private Integer ticketsResolus;
    private Double noteMoyenne;
    private Integer totalPoints;
}
