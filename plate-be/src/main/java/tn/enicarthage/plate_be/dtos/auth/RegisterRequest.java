package tn.enicarthage.plate_be.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.enicarthage.plate_be.entities.ROLE;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private ROLE role;
}