package tn.enicarthage.plate_be.auth;

import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService service;

    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }

public class AuthenticationService {


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        throw new UnsupportedOperationException("authenticate(...) not implemented yet");
    }

    public @Nullable Object register(RegisterRequest request) {
        throw new UnsupportedOperationException("Unimplemented method 'register'");
    }

}
    @PostMapping("/register")
    public ResponseEntity<@Nullable Object> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
