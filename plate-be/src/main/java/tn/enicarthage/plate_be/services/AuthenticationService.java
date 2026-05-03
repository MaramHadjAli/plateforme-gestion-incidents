package tn.enicarthage.plate_be.services;

import jakarta.validation.Valid;
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
import java.util.Date;
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

    public AuthenticationResponse register(@Valid RegisterRequest request) {
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
                tech.setPrenom(request.getPrenom());
                tech.setEmail(request.getEmail());
                tech.setMotPassee(encodedPassword);
                tech.setRole(ROLE.TECHNICIEN);
                tech.setEnabled(true);
                tech.setActive(true);
                tech.setDateInscription(new Date());
                savedUser = technicienRepository.save(tech);
                break;
            case ADMIN:
                Adminstrateur admin = new Adminstrateur();
                admin.setNom(request.getNom());
                admin.setPrenom(request.getPrenom());
                admin.setEmail(request.getEmail());
                admin.setMotPassee(encodedPassword);
                admin.setRole(ROLE.ADMIN);
                admin.setEnabled(true);
                admin.setActive(true);
                admin.setDateInscription(new Date());
                savedUser = userRepository.save(admin);
                break;
            default:
                Demandeur demandeur = new Demandeur();
                demandeur.setNom(request.getNom());
                demandeur.setPrenom(request.getPrenom());
                demandeur.setEmail(request.getEmail());
                demandeur.setMotPassee(encodedPassword);
                demandeur.setRole(ROLE.DEMANDEUR);
                demandeur.setEnabled(true);
                demandeur.setActive(true);
                demandeur.setDateInscription(new Date());
                savedUser = demandeurRepository.save(demandeur);
                break;
        }

        logAction(request.getEmail(), "REGISTER", "SUCCESS", "Nouvel utilisateur inscrit");

        String confirmationLink = "http://localhost:4200/confirm-email?token=" + UUID.randomUUID().toString();
        try {
            emailService.sendConfirmationEmail(savedUser.getEmail(), savedUser.getNom(), confirmationLink);
        } catch (RuntimeException ex) {
            System.err.println("Echec envoi email confirmation: " + ex.getMessage());
        }

        // Generate tokens for automatic login
        String jwt = jwtUtil.generateToken(savedUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getId());

        AuthenticationResponse.UserInfo userInfo = new AuthenticationResponse.UserInfo(
                savedUser.getId(),
                savedUser.getNom(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );

        return new AuthenticationResponse(jwt, refreshToken.getToken(), userInfo);
    }

    public AuthenticationResponse login(LoginRequest request) {
        String email = request.getEmail();

        Utilisateur user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            logAction(email, "LOGIN_FAILED", "FAILED", "Utilisateur non trouvé");
            throw new RuntimeException("User not found");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logAction(email, "LOGIN_FAILED", "FAILED", "Mot de passe incorrect");
            throw new RuntimeException("Invalid password");
        }

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

    private void logAction(String email, String action, String status, String details) {
        try {
            HttpServletRequest request = null;
            try {
                request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
            } catch (Exception e) {

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
            System.err.println("Erreur lors du logging: " + e.getMessage());
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

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