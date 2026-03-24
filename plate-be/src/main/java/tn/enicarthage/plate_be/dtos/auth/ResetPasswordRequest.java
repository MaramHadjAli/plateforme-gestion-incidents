package tn.enicarthage.plate_be.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
    private String confirmPassword;
}

