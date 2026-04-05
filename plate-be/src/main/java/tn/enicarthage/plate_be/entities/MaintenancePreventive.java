package tn.enicarthage.plate_be.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "maintenance_preventive")
public class MaintenancePreventive {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idMaintenance;

	@Temporal(TemporalType.DATE)
	private Date dateProgramme;

	@Column(length = 50)
	private String frequence;

	@Lob
	private String description;

	@Column(length = 30)
	private String status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "equipement_id")
	private Equipement equipement;

	public MaintenancePreventive() {
	}

	public Long getIdMaintenance() {
		return idMaintenance;
	}

	public void setIdMaintenance(Long idMaintenance) {
		this.idMaintenance = idMaintenance;
	}

	public Date getDateProgramme() {
		return dateProgramme;
	}

	public void setDateProgramme(Date dateProgramme) {
		this.dateProgramme = dateProgramme;
	}

	public String getFrequence() {
		return frequence;
	}

	public void setFrequence(String frequence) {
		this.frequence = frequence;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Equipement getEquipement() {
		return equipement;
	}

	public void setEquipement(Equipement equipement) {
		this.equipement = equipement;
	}
}
