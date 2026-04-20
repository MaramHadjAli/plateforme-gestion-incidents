package tn.enicarthage.modules.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.security.JwtService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // In prod define specific origins
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
         this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Since we are mocking the Spring Security AuthManager for brevity while giving real production-ready JWT output:
        // In a real scenario, you use `authenticationManager.authenticate(...)`
        
        // MOCK AUTHENTICATION CHECK
        if (!"admin".equals(loginRequest.getUsername()) && !"tech".equals(loginRequest.getUsername())) {
             return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        String role = "admin".equals(loginRequest.getUsername()) ? "ROLE_ADMIN" : "ROLE_TECHNICIAN";
        
        // Generate Token
        String token = jwtService.generateToken(loginRequest.getUsername(), role);
        
        AuthResponse response = new AuthResponse(token, loginRequest.getUsername(), role);
        return ResponseEntity.ok(response);
    }
}

class LoginRequest {
    private String username;
    private String password;
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

class AuthResponse {
    private String token;
    private String username;
    private String role;
    public AuthResponse(String token, String username, String role) {
         this.token = token; this.username = username; this.role = role;
    }
    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}
