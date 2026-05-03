package tn.enicarthage.plate_be.auth;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.plate_be.dtos.auth.*;
import tn.enicarthage.plate_be.entities.RefreshToken;
import tn.enicarthage.plate_be.exceptions.PasswordResetException;
import tn.enicarthage.plate_be.services.AuthenticationService;
import tn.enicarthage.plate_be.services.RefreshTokenService;
import tn.enicarthage.plate_be.services.PasswordResetService;
import tn.enicarthage.plate_be.security.JwtUtil;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService service;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetService passwordResetService;
    private final JwtUtil jwtUtil;


    public AuthenticationController(
            AuthenticationService service,
            RefreshTokenService refreshTokenService,
            PasswordResetService passwordResetService,
            JwtUtil jwtUtil
    ) {
        this.service = service;
        this.refreshTokenService = refreshTokenService;
        this.passwordResetService = passwordResetService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @GetMapping("/confirm-email")
    public ResponseEntity<?> confirmEmail(@RequestParam String token) {
        try {
            service.confirmAccount(token);
            return ResponseEntity.ok("Votre compte a été activé avec succès. Vous pouvez maintenant vous connecter.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(service.login(request));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtil.generateTokenFromEmail(user.getEmail());
                    return ResponseEntity.ok(new RefreshTokenResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            passwordResetService.initiateForgotPassword(request.getEmail());
            return ResponseEntity.ok(new PasswordResetResponse(
                    true,
                    "Un email de réinitialisation a été envoyé à votre adresse email"
            ));
        } catch (PasswordResetException e) {
            return ResponseEntity.ok(new PasswordResetResponse(
                    false,
                    e.getMessage()
            ));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest().body(new PasswordResetResponse(
                        false,
                        "Les mots de passe ne correspondent pas"
                ));
            }

            if (request.getNewPassword().length() < 6) {
                return ResponseEntity.badRequest().body(new PasswordResetResponse(
                        false,
                        "Le mot de passe doit contenir au moins 6 caractères"
                ));
            }

            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(new PasswordResetResponse(
                    true,
                    "Votre mot de passe a été réinitialisé avec succès"
            ));
        } catch (PasswordResetException e) {
            return ResponseEntity.badRequest().body(new PasswordResetResponse(
                    false,
                    e.getMessage()
            ));
        }
    }

    @GetMapping("/verify-reset-token")
    public ResponseEntity<?> verifyResetToken(@RequestParam String token) {
        System.out.println("Verifying token: " + token);
        boolean isValid = passwordResetService.verifyToken(token);
        System.out.println("Token valid: " + isValid);
        if (isValid) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Token invalide ou expiré");
        }
    }
}
