package tn.enicarthage.plate_be.dtos.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthenticationResponse {

    private String token;
    private String refreshToken;
    private String role;
    private String email;

    public AuthenticationResponse(String token, String refreshToken, String role, String email) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.role = role;
        this.email = email;
    }

}
