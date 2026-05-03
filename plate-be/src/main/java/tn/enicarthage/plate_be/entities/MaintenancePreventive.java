package tn.enicarthage.plate_be.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "maintenance_preventive")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenancePreventive {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idMaintenance;

	@Temporal(TemporalType.DATE)
	private Date dateProgramme;

	@Enumerated(EnumType.STRING)
	private FREQUENCE_MAINTENANCE frequence;

	@Lob
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(length = 30)
	private STATUS_MAINTENANCE status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "equipement_id")
	private Equipement equipement;
}
