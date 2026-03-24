package tn.enicarthage.plate_be.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.entities.PasswordResetToken;
import tn.enicarthage.plate_be.entities.Utilisateur;
import tn.enicarthage.plate_be.exceptions.PasswordResetException;
import tn.enicarthage.plate_be.repositories.PasswordResetTokenRepository;
import tn.enicarthage.plate_be.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // Token valide pendant 15 minutes
    private static final int RESET_TOKEN_EXPIRY_MINUTES = 15;

    public PasswordResetService(
            PasswordResetTokenRepository tokenRepository,
            UserRepository userRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder
    ) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }


    public void initiateForgotPassword(String email) {
        Utilisateur user = userRepository.findByEmail(email)
                .orElseThrow(() -> new PasswordResetException("Utilisateur non trouvé avec l'email: " + email));

        String resetToken = UUID.randomUUID().toString();

        PasswordResetToken token = PasswordResetToken.builder()
                .token(resetToken)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(RESET_TOKEN_EXPIRY_MINUTES))
                .used(false)
                .build();

        tokenRepository.save(token);

        String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;

        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new PasswordResetException("Token de réinitialisation invalide"));

        if (!resetToken.isValid()) {
            if (resetToken.isExpired()) {
                throw new PasswordResetException("Le token de réinitialisation a expiré");
            } else {
                throw new PasswordResetException("Le token de réinitialisation a déjà été utilisé");
            }
        }

        Utilisateur user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }


    public boolean validateResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElse(null);

        return resetToken != null && resetToken.isValid();
    }
}

