package tn.enicarthage.plate_be.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPreference;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", unique = true)
    private Utilisateur utilisateur;


    private boolean ticketAssignedEnabled;
    private boolean statusChangedEnabled;
    private boolean maintenanceReminderEnabled;
    private boolean badgeAwardedEnabled;
    private boolean slaExceededEnabled;


    private boolean pushNotificationEnabled;
    private boolean emailEnabled;
    private boolean smsEnabled;


    private String emailDigestFrequency;


    private boolean smsCriticalOnly;


    private String quietHoursStart;
    private String quietHoursEnd;

    @Builder.Default
    private boolean allNotificationsEnabled = true;
}

