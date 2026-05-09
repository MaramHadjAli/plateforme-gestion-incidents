package tn.enicarthage.plate_be.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.entities.Notification;

@Service
@Slf4j
public class SMSService {

    @Value("${app.sms.enabled:false}")
    private boolean smsEnabled;

    @Value("${app.sms.provider:twilio}")
    private String smsProvider;


    
    public void sendCriticalNotificationSMS(Notification notification) {
        if (!smsEnabled) {
            log.debug("SMS notifications are disabled");
            return;
        }

        try {
            if (notification.getUtilisateur() == null || notification.getUtilisateur().getTelephone() == null) {
                log.warn("Cannot send SMS: User phone not found for notification {}", notification.getIdNotification());
                return;
            }

            String phoneNumber = notification.getUtilisateur().getTelephone();
            String smsMessage = buildSMSMessage(notification);


            sendSMS(phoneNumber, smsMessage);

            notification.setSmsSent(true);
            log.info("SMS sent successfully for notification {}", notification.getIdNotification());
        } catch (Exception e) {
            log.error("Error sending SMS for notification {}", notification.getIdNotification(), e);
        }
    }

    public void sendCriticalTicketAlertSMS(String phoneNumber, String ticketId, String title) {
        if (!smsEnabled) {
            log.debug("SMS notifications are disabled");
            return;
        }

        try {
            String message = String.format("[URGENT] %s (Ticket: %s). Intervention rapide requise!", title, ticketId);
            sendSMS(phoneNumber, message);
            log.info("Critical ticket alert SMS sent to {}", phoneNumber);
        } catch (Exception e) {
            log.error("Error sending critical SMS to {}", phoneNumber, e);
        }
    }

    public void sendSLAExceededSMS(String phoneNumber, String ticketId) {
        if (!smsEnabled) {
            log.debug("SMS notifications are disabled");
            return;
        }

        try {
            String message = String.format("[SLA DÉPASSÉ] Le SLA du ticket %s a été dépassé. Action immédiate requise!", ticketId);
            sendSMS(phoneNumber, message);
            log.info("SLA exceeded SMS sent to {}", phoneNumber);
        } catch (Exception e) {
            log.error("Error sending SLA SMS to {}", phoneNumber, e);
        }
    }

    private String buildSMSMessage(Notification notification) {
        return String.format("[%s] %s: %s",
                notification.getSeverity(),
                notification.getTitle(),
                notification.getMessage().substring(0, Math.min(100, notification.getMessage().length())));
    }

    private void sendSMS(String phoneNumber, String message) {


        log.info("SMS to {}: {}", phoneNumber, message);








    }
}

