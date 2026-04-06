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

    // Types of notifications
    private boolean ticketAssignedEnabled;
    private boolean statusChangedEnabled;
    private boolean maintenanceReminderEnabled;
    private boolean badgeAwardedEnabled;
    private boolean slaExceededEnabled;

    // Channels
    private boolean pushNotificationEnabled;
    private boolean emailEnabled;
    private boolean smsEnabled;

    // Email digest preferences
    private String emailDigestFrequency; // DAILY, WEEKLY, NEVER

    // SMS preferences (critical tickets only)
    private boolean smsCriticalOnly;

    // Quiet hours
    private String quietHoursStart; // HH:mm format
    private String quietHoursEnd;   // HH:mm format

    @Builder.Default
    private boolean allNotificationsEnabled = true;
}

