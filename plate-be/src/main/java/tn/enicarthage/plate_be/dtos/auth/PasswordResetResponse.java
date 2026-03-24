package tn.enicarthage.plate_be.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PasswordResetResponse {
    private boolean success;
    private String message;

    public PasswordResetResponse(boolean success) {
        this.success = success;
    }
}

