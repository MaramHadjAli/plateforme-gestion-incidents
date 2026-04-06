package tn.enicarthage.plate_be.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.entities.Notification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@Slf4j
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

    // 📧 NOTIFICATION EMAILS METHODS

    public void sendNotificationEmail(Notification notification) {
        try {
            if (notification.getUtilisateur() == null || notification.getUtilisateur().getEmail() == null) {
                log.warn("Cannot send email: User email not found for notification {}", notification.getIdNotification());
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(notification.getUtilisateur().getEmail());
            helper.setSubject("🔔 " + notification.getTitle());

            String htmlContent = buildNotificationEmailTemplate(notification);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            notification.setEmailSent(true);
            log.info("Email notification sent successfully for {}", notification.getIdNotification());
        } catch (MessagingException e) {
            log.error("Error sending email notification {}", notification.getIdNotification(), e);
        }
    }

    public void sendDailyDigest(String userEmail, String digestContent, int notificationCount) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(userEmail);
            helper.setSubject("[Résumé Quotidien] " + notificationCount + " notifications");

            String htmlContent = buildDailyDigestTemplate(digestContent, notificationCount);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Daily digest email sent to {}", userEmail);
        } catch (MessagingException e) {
            log.error("Error sending daily digest to {}", userEmail, e);
        }
    }

    public void sendCriticalAlertEmail(Notification notification) {
        try {
            if (notification.getUtilisateur() == null || notification.getUtilisateur().getEmail() == null) {
                log.warn("Cannot send alert email: User email not found");
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(notification.getUtilisateur().getEmail());
            helper.setSubject("⚠️ ALERTE CRITIQUE: " + notification.getTitle());

            String htmlContent = buildCriticalAlertTemplate(notification);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Critical alert email sent for ticket {}", notification.getTicketId());
        } catch (MessagingException e) {
            log.error("Error sending critical alert email", e);
        }
    }

    private String buildNotificationEmailTemplate(Notification notification) {
        String severityColor = switch (notification.getSeverity()) {
            case "CRITICAL" -> "#e74c3c";
            case "ERROR" -> "#e67e22";
            case "WARNING" -> "#f39c12";
            default -> "#3498db";
        };

        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 0; background-color: #f5f5f5; }" +
                ".header { background: linear-gradient(135deg, " + severityColor + " 0%, " + severityColor + " 100%); color: white; padding: 25px; text-align: center; }" +
                ".content { padding: 25px; background-color: white; margin: 15px; border-radius: 5px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }" +
                ".title { font-size: 20px; font-weight: bold; margin-bottom: 10px; color: " + severityColor + "; }" +
                ".meta { font-size: 13px; color: #666; margin: 10px 0; }" +
                ".message { line-height: 1.6; color: #333; margin: 15px 0; }" +
                ".footer { text-align: center; color: #999; font-size: 12px; padding: 15px; border-top: 1px solid #eee; }" +
                ".btn { background-color: " + severityColor + "; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px; display: inline-block; margin: 10px 0; }" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<div class='header'><h1>Notification: " + notification.getType() + "</h1></div>" +
                "<div class='content'>" +
                "<div class='title'>" + notification.getTitle() + "</div>" +
                "<div class='meta'><strong>Type:</strong> " + notification.getType() + " | <strong>Sévérité:</strong> " + notification.getSeverity() + "</div>" +
                (notification.getTicketId() != null ? "<div class='meta'><strong>Ticket:</strong> " + notification.getTicketId() + "</div>" : "") +
                "<div class='message'>" + notification.getMessage() + "</div>" +
                "</div>" +
                "<div class='footer'><p>&copy; 2026 Plateforme de Gestion d'Incidents</p></div>" +
                "</div></body></html>";
    }

    private String buildDailyDigestTemplate(String digestContent, int count) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background-color: #3498db; color: white; padding: 20px; text-align: center; border-radius: 5px; }" +
                ".content { padding: 20px; background-color: #f5f5f5; margin-top: 15px; border-radius: 5px; }" +
                ".notification-item { background-color: white; padding: 15px; margin: 10px 0; border-left: 4px solid #3498db; border-radius: 3px; }" +
                ".footer { text-align: center; color: #999; font-size: 12px; margin-top: 20px; }" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<div class='header'><h1>📋 Résumé Quotidien</h1><p>" + count + " notifications</p></div>" +
                "<div class='content'>" + digestContent + "</div>" +
                "<div class='footer'><p>&copy; 2026 Plateforme de Gestion d'Incidents</p></div>" +
                "</div></body></html>";
    }

    private String buildCriticalAlertTemplate(Notification notification) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><style>" +
                "body { font-family: Arial, sans-serif; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 0; }" +
                ".header { background-color: #c0392b; color: white; padding: 25px; text-align: center; font-size: 24px; font-weight: bold; }" +
                ".alert { background-color: #ffe6e6; color: #c0392b; padding: 20px; margin: 15px; border: 2px solid #c0392b; border-radius: 5px; }" +
                ".ticket { background-color: white; padding: 20px; margin: 15px; border-left: 4px solid #e74c3c; }" +
                ".action-needed { font-weight: bold; color: #c0392b; }" +
                ".footer { text-align: center; color: #666; font-size: 12px; padding: 15px; }" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<div class='header'>⚠️ ALERTE CRITIQUE</div>" +
                "<div class='alert'>" +
                "<p class='action-needed'>⚡ Une intervention immédiate est requise!</p>" +
                "</div>" +
                "<div class='ticket'>" +
                "<h2>" + notification.getTitle() + "</h2>" +
                "<p><strong>Ticket:</strong> " + notification.getTicketId() + "</p>" +
                "<p><strong>Message:</strong></p>" +
                "<p>" + notification.getMessage() + "</p>" +
                "</div>" +
                "<div class='footer'><p>&copy; 2026 Plateforme de Gestion d'Incidents. Urgence!</p></div>" +
                "</div></body></html>";
    }
}