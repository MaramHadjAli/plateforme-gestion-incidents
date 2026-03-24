package tn.enicarthage.plate_be.auth;

import lombok.Getter;
import lombok.Setter;
import tn.enicarthage.plate_be.entities.ROLE;
import jakarta.validation.constraints.*;

@Getter
public class RegisterRequest {
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit avoir entre 2 et 100 caractères")
    private final String nom;
    
    @Setter
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit avoir au moins 6 caractères")
    private final String password;
    
    @Setter
    @NotNull(message = "Le rôle est obligatoire")
    private ROLE role;

    public RegisterRequest(String nom, String email, String password, ROLE role) {
        this.nom = nom;
        this.email = email;
        this.password = password;
        this.role = role;
    }

}
