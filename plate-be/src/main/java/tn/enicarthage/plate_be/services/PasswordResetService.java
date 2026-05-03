package tn.enicarthage.plate_be.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.entities.Utilisateur;
import tn.enicarthage.plate_be.exceptions.PasswordResetException;
import tn.enicarthage.plate_be.repositories.UserRepository;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private final Map<String, ResetTokenInfo> resetTokens = new ConcurrentHashMap<>();

    private static final long TOKEN_EXPIRY_MS = 15 * 60 * 1000;

    public void initiateForgotPassword(String email) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new PasswordResetException("Email non trouvé"));

        String token = generateResetToken(email);
        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        emailService.sendPasswordResetEmail(email, resetLink);
    }

    public String generateResetToken(String email) {
        String token = java.util.UUID.randomUUID().toString();
        ResetTokenInfo info = new ResetTokenInfo();
        info.setEmail(email);
        info.setExpiryDate(new Date(System.currentTimeMillis() + TOKEN_EXPIRY_MS));
        resetTokens.put(token, info);
        return token;
    }

    public boolean verifyToken(String token) {
        ResetTokenInfo info = resetTokens.get(token);
        if (info == null) {
            return false;
        }

        Date expiry = info.getExpiryDate();
        if (expiry == null || expiry.before(new Date())) {
            resetTokens.remove(token);
            return false;
        }

        return true;
    }

    public boolean validateResetToken(String token) {
        return verifyToken(token);
    }

    public String getEmailByToken(String token) {
        if (!verifyToken(token)) {
            return null;
        }
        ResetTokenInfo info = resetTokens.get(token);
        return info != null ? info.getEmail() : null;
    }

    public void resetPassword(String token, String newPassword) {
        String email = getEmailByToken(token);
        if (email == null) {
            throw new PasswordResetException("Token invalide ou expiré");
        }

        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new PasswordResetException("Utilisateur non trouvé"));

        user.setMotPassee(passwordEncoder.encode(newPassword));
        utilisateurRepository.save(user);

        resetTokens.remove(token);
    }

    public void invalidateToken(String token) {
        resetTokens.remove(token);
    }

    private static class ResetTokenInfo {
        private String email;
        private Date expiryDate;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Date getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(Date expiryDate) {
            this.expiryDate = expiryDate;
        }
    }
}
