package tn.enicarthage.plate_be.dtos.auth;

import lombok.Getter;

public class RegisterRequest {
    public String email;
    @Getter
    private String nom;
    @Getter
    private String password;

}