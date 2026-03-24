package tn.enicarthage.plate_be.dtos.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";

    public RefreshTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
