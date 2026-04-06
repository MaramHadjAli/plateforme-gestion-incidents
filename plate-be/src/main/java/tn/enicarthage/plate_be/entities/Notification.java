package tn.enicarthage.plate_be.entities;

import jakarta.persistence.*;
import java.util.Date;
import lombok.*;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idNotification;

	@Column(length = 50)
	private String type; // TICKET_ASSIGNED, STATUS_CHANGED, MAINTENANCE_REMINDER, BADGE_AWARDED, SLA_EXCEEDED

	@Lob
	private String title;

	@Lob
	private String message;

	@Column(length = 20)
	private String severity; // INFO, WARNING, ERROR, CRITICAL

	@Temporal(TemporalType.TIMESTAMP)
	private Date dateEnvoi;

	private boolean isRead;

	private boolean emailSent;
	private boolean smsSent;

	@Column(length = 50)
	private String ticketId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "utilisateur_id")
	private Utilisateur utilisateur;

	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = new Date();
		dateEnvoi = new Date();
	}
}
