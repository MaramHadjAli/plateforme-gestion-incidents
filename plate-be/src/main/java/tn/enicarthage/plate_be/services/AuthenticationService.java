package tn.enicarthage.plate_be.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import tn.enicarthage.plate_be.dtos.auth.*;
import tn.enicarthage.plate_be.entities.*;
import tn.enicarthage.plate_be.entities.ROLE;
import tn.enicarthage.plate_be.repositories.*;
import tn.enicarthage.plate_be.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TraceLoginRepository traceRepo;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            TraceLoginRepository traceRepo,
            EmailService emailService,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.traceRepo = traceRepo;
        this.emailService = emailService;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthenticationResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }

        Utilisateur user;

        switch (request.getRole()) {
            case DEMANDEUR:
                user = new Demandeur();
                break;
            case TECHNICIEN:
                user = new Technicien();
                break;
            case ADMIN:
                user = new Adminstrateur();
                break;
            default:
                user = new Utilisateur();
        }

        user.setNom(request.getNom());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEnabled(true);

        Utilisateur savedUser = userRepository.save(user);

        // Log l'enregistrement
        logAction(request.getEmail(), "REGISTER", "SUCCESS", "Nouvel utilisateur inscrit");

        // Envoi email (optionnel, ne bloque pas)
        try {
            String confirmationLink = "http://localhost:4200/confirm-email?token=" + UUID.randomUUID().toString();
            emailService.sendConfirmationEmail(user.getEmail(), user.getNom(), confirmationLink);
        } catch (Exception ex) {
            System.err.println("Echec envoi email confirmation: " + ex.getMessage());
        }

        // Auto-login après inscription
        String jwt = jwtUtil.generateToken(savedUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getId());

        return new AuthenticationResponse(jwt, refreshToken.getToken(), savedUser.getRole().name(), savedUser.getEmail());
    }

    public AuthenticationResponse login(LoginRequest request) {
        String email = request.getEmail();

        Utilisateur user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            logAction(email, "LOGIN_FAILED", "FAILED", "Utilisateur non trouvé");
            throw new RuntimeException("User not found");
        }

        // Vérifie le mot de passe
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // Log tentative de mot de passe incorrect
            logAction(email, "LOGIN_FAILED", "FAILED", "Mot de passe incorrect");
            throw new RuntimeException("Invalid password");
        }

        String jwt = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthenticationResponse(jwt, refreshToken.getToken(), user.getRole().name(), user.getEmail());
    }

    /**
     * Enregistre une action sensible dans la base de données
     */
    private void logAction(String email, String action, String status, String details) {
        try {
            HttpServletRequest request = null;
            try {
                request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
            } catch (Exception e) {
                // En cas d'erreur (ex: pas de contexte HTTP), on continue sans IP
            }

            TraceLogin log = TraceLogin.builder()
                    .email(email)
                    .action(action)
                    .status(status)
                    .date(LocalDateTime.now())
                    .details(details)
                    .ipAddress(getClientIpAddress(request))
                    .userAgent(request != null ? request.getHeader("User-Agent") : null)
                    .build();

            traceRepo.save(log);
        } catch (Exception e) {
            // Log l'erreur mais ne pas arrêter le processus d'authentification
            System.err.println("Erreur lors du logging: " + e.getMessage());
        }
    }

    /**
     * Récupère l'adresse IP du client
     */
    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        // Essaie plusieurs headers possibles
        String[] headers = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }
}