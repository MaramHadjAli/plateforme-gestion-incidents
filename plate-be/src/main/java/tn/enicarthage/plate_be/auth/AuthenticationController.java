package tn.enicarthage.plate_be.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.enicarthage.plate_be.dtos.auth.LoginRequest;
import tn.enicarthage.plate_be.dtos.auth.RefreshTokenRequest;
import tn.enicarthage.plate_be.dtos.auth.RefreshTokenResponse;
import tn.enicarthage.plate_be.entities.RefreshToken;
import tn.enicarthage.plate_be.services.AuthenticationService;
import tn.enicarthage.plate_be.services.RefreshTokenService;
import tn.enicarthage.plate_be.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService service;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;


    public AuthenticationController(AuthenticationService service, RefreshTokenService refreshTokenService, JwtUtil jwtUtil) {
        this.service = service;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(service.login(request));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@RequestBody RefreshTokenRequest request) {
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
}
