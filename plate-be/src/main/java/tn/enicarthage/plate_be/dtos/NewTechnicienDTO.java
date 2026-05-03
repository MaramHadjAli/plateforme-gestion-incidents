package tn.enicarthage.plate_be.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewTechnicienDTO {

    @NotBlank(message = "Nom is required")
    private String nom;

    @NotBlank(message = "Prénom is required")
    private String prenom;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String telephone;

    private String specialite;

    @NotBlank(message = "Role is required")
    private String role;
}
