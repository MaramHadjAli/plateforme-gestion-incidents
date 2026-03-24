package tn.enicarthage.plate_be.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromEmail;

    public void sendConfirmationEmail(String to, String username, String confirmationLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Confirmation d'inscription - Plateforme de Gestion d'Incidents");

            String htmlContent = buildConfirmationEmailTemplate(username, confirmationLink);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi d'email: " + e.getMessage(), e);
        }
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Réinitialisation de mot de passe - Plateforme de Gestion d'Incidents");

            String htmlContent = buildPasswordResetEmailTemplate(resetLink);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi d'email: " + e.getMessage(), e);
        }
    }

    public void sendAccountLockedNotification(String to, String reason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Alerte Sécurité - Compte Verrouillé");

            String htmlContent = buildAccountLockedTemplate(reason);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi d'email: " + e.getMessage(), e);
        }
    }

    private String buildConfirmationEmailTemplate(String username, String confirmationLink) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><style>" +
                "body { font-family: Arial, sans-serif; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background-color: #3498db; color: white; padding: 20px; text-align: center; }" +
                ".content { padding: 20px; background-color: #f5f5f5; }" +
                ".button { background-color: #27ae60; color: white; padding: 12px 30px; text-decoration: none; display: inline-block; margin: 20px 0; }" +
                ".footer { text-align: center; color: #666; font-size: 12px; margin-top: 20px; }" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<div class='header'><h1>Bienvenue sur la Plateforme de Gestion d'Incidents</h1></div>" +
                "<div class='content'>" +
                "<p>Bonjour <strong>" + username + "</strong>,</p>" +
                "<p>Merci de vous être inscrit sur notre plateforme. Pour activer votre compte, veuillez cliquer sur le lien ci-dessous :</p>" +
                "<a href='" + confirmationLink + "' class='button'>Confirmer mon compte</a>" +
                "<p>Ce lien expire dans 24 heures.</p>" +
                "<p>Si vous n'avez pas créé ce compte, veuillez ignorer cet email.</p>" +
                "</div>" +
                "<div class='footer'><p>&copy; 2026 Plateforme de Gestion d'Incidents. Tous droits réservés.</p></div>" +
                "</div></body></html>";
    }

    private String buildPasswordResetEmailTemplate(String resetLink) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><style>" +
                "body { font-family: Arial, sans-serif; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background-color: #e74c3c; color: white; padding: 20px; text-align: center; }" +
                ".content { padding: 20px; background-color: #f5f5f5; }" +
                ".button { background-color: #3498db; color: white; padding: 12px 30px; text-decoration: none; display: inline-block; margin: 20px 0; }" +
                ".footer { text-align: center; color: #666; font-size: 12px; margin-top: 20px; }" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<div class='header'><h1>Réinitialisation de Mot de Passe</h1></div>" +
                "<div class='content'>" +
                "<p>Nous avons reçu une demande de réinitialisation de votre mot de passe.</p>" +
                "<p>Cliquez sur le lien ci-dessous pour créer un nouveau mot de passe :</p>" +
                "<a href='" + resetLink + "' class='button'>Réinitialiser mon mot de passe</a>" +
                "<p><strong>Ce lien expire dans 15 minutes.</strong></p>" +
                "<p>Si vous n'avez pas demandé cette réinitialisation, ignorez cet email et votre mot de passe restera inchangé.</p>" +
                "</div>" +
                "<div class='footer'><p>&copy; 2026 Plateforme de Gestion d'Incidents. Tous droits réservés.</p></div>" +
                "</div></body></html>";
    }

    private String buildAccountLockedTemplate(String reason) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><style>" +
                "body { font-family: Arial, sans-serif; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background-color: #c0392b; color: white; padding: 20px; text-align: center; }" +
                ".content { padding: 20px; background-color: #f5f5f5; }" +
                ".footer { text-align: center; color: #666; font-size: 12px; margin-top: 20px; }" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<div class='header'><h1>Alerte Sécurité</h1></div>" +
                "<div class='content'>" +
                "<p><strong>Votre compte a été verrouillé.</strong></p>" +
                "<p>Raison : " + reason + "</p>" +
                "<p>Si vous pensez qu'il y a une erreur, veuillez contacter l'administrateur système.</p>" +
                "</div>" +
                "<div class='footer'><p>&copy; 2026 Plateforme de Gestion d'Incidents. Tous droits réservés.</p></div>" +
                "</div></body></html>";
    }
}
