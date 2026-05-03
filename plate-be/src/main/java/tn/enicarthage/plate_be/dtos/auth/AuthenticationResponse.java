package tn.enicarthage.plate_be.dtos.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthenticationResponse {

    private String accessToken;
    private String refreshToken;
    private UserInfo user;

    public AuthenticationResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public AuthenticationResponse(String accessToken, String refreshToken, UserInfo user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    @Getter
    @Setter
    public static class UserInfo {
        private Long id;
        private String nom;
        private String email;
        private String role;

        public UserInfo(Long id, String nom, String email, String role) {
            this.id = id;
            this.nom = nom;
            this.email = email;
            this.role = role;
        }
    }
}
