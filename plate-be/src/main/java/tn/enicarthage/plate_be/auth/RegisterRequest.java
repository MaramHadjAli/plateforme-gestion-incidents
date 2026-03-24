package tn.enicarthage.plate_be.auth;

import lombok.Getter;
import lombok.Setter;
import tn.enicarthage.plate_be.entities.ROLE;

@Getter
public class RegisterRequest {
    private final String nom;
    @Setter
    private String email;
    private final String password;
    @Setter
    private ROLE role;

    public RegisterRequest(String nom, String email, String password, ROLE role) {
        this.nom = nom;
        this.email = email;
        this.password = password;
        this.role = role;
    }

}
