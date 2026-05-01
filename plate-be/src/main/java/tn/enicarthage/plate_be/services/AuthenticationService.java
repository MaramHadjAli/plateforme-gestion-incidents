package tn.enicarthage.plate_be.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import tn.enicarthage.plate_be.auth.RegisterRequest;
import tn.enicarthage.plate_be.dtos.auth.*;
import tn.enicarthage.plate_be.entities.*;
import tn.enicarthage.plate_be.repositories.*;
import tn.enicarthage.plate_be.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final DemandeurRepository demandeurRepository;
    private final TechnicienRepository technicienRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TraceLoginRepository traceRepo;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationService(
            UserRepository userRepository,
            DemandeurRepository demandeurRepository,
            TechnicienRepository technicienRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            TraceLoginRepository traceRepo,
            EmailService emailService,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.demandeurRepository = demandeurRepository;
        this.technicienRepository = technicienRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.traceRepo = traceRepo;
        this.emailService = emailService;
        this.refreshTokenService = refreshTokenService;
    }

    public Utilisateur register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        ROLE role = request.getRole() != null ? request.getRole() : ROLE.DEMANDEUR;

        Utilisateur savedUser;

        switch (role) {
            case TECHNICIEN:
                Technicien tech = new Technicien();
                tech.setNom(request.getNom());
                tech.setEmail(request.getEmail());
                tech.setPassword(encodedPassword);
                tech.setRole(ROLE.TECHNICIEN);
                tech.setEnabled(true);
                savedUser = technicienRepository.save(tech);
                break;
            case ADMIN:
                Adminstrateur admin = new Adminstrateur();
                admin.setNom(request.getNom());
                admin.setEmail(request.getEmail());
                admin.setPassword(encodedPassword);
                admin.setRole(ROLE.ADMIN);
                admin.setEnabled(true);
                savedUser = userRepository.save(admin);
                break;
            default:
                Demandeur demandeur = new Demandeur();
                demandeur.setNom(request.getNom());
                demandeur.setEmail(request.getEmail());
                demandeur.setPassword(encodedPassword);
                demandeur.setRole(ROLE.DEMANDEUR);
                demandeur.setEnabled(true);
                savedUser = demandeurRepository.save(demandeur);
                break;
        }

        // Log l'enregistrement
        logAction(request.getEmail(), "REGISTER", "SUCCESS", "Nouvel utilisateur inscrit");

        String confirmationLink = "http://localhost:4200/confirm-email?token=" + UUID.randomUUID().toString();
        try {
            emailService.sendConfirmationEmail(savedUser.getEmail(), savedUser.getNom(), confirmationLink);
        } catch (RuntimeException ex) {
            // Ne pas bloquer l'inscription si le SMTP n'est pas configure.
            System.err.println("Echec envoi email confirmation: " + ex.getMessage());
        }

        return savedUser;
    }

    public AuthenticationResponse login(LoginRequest request) {
        String email = request.getEmail();

        // Vérifie si l'utilisateur existe
        Utilisateur user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            // Log tentative avec email inexistant
            logAction(email, "LOGIN_FAILED", "FAILED", "Utilisateur non trouvé");
            throw new RuntimeException("User not found");
        }

        // Vérifie le mot de passe
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // Log tentative de mot de passe incorrect
            logAction(email, "LOGIN_FAILED", "FAILED", "Mot de passe incorrect");
            throw new RuntimeException("Invalid password");
        }

        // Log la connexion réussie
        logAction(email, "LOGIN_SUCCESS", "SUCCESS", "Connexion réussie");

        String jwt = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        AuthenticationResponse.UserInfo userInfo = new AuthenticationResponse.UserInfo(
                user.getId(),
                user.getNom(),
                user.getEmail(),
                user.getRole().name()
        );

        return new AuthenticationResponse(jwt, refreshToken.getToken(), userInfo);
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