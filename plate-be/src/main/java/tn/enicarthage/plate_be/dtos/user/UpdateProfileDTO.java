package tn.enicarthage.plate_be.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileDTO {
    private String nom;
    private String prenom;
    private String telephone;
}
