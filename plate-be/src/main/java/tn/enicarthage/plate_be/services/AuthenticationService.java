package tn.enicarthage.plate_be.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tn.enicarthage.plate_be.auth.RegisterRequest;
import tn.enicarthage.plate_be.dtos.auth.*;
import tn.enicarthage.plate_be.entities.*;
import tn.enicarthage.plate_be.repositories.*;
import tn.enicarthage.plate_be.security.JwtUtil;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TraceLoginRepository traceRepo;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;

    public Utilisateur register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }

        Utilisateur user = Utilisateur.builder()
                .nom(request.getNom())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(false)
                .build();

        Utilisateur savedUser = userRepository.save(user);

        String confirmationLink = "http://localhost:4200/confirm-email?token=" + UUID.randomUUID().toString();
        emailService.sendConfirmationEmail(user.getEmail(), user.getNom(), confirmationLink);

        return savedUser;
    }

    public AuthenticationResponse login(LoginRequest request) {

        Utilisateur user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        TraceLogin log = new TraceLogin();
        log.setEmail(user.getEmail());
        log.setAction("LOGIN_SUCCESS");
        log.setDate(LocalDateTime.now());
        traceRepo.save(log);

        String jwt = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthenticationResponse(jwt, refreshToken.getToken());
    }
}