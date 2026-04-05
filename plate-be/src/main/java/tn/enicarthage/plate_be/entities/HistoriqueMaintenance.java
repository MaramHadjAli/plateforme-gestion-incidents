package tn.enicarthage.plate_be.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "historique_maintenance")
public class HistoriqueMaintenance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idHistorique;

	@Temporal(TemporalType.TIMESTAMP)
	private Date dateExecution;

	@Column(length = 50)
	private String type;

	@Lob
	private String description;

	private float coutMaintenance;

	@Column(length = 100)
	private String nomTechnicienResponsable;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "equipement_id")
	private Equipement equipement;



	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "technicien_id")
	private Technicien technicien;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_id")
	private Ticket ticket;


	public HistoriqueMaintenance() {
	}


	public Long getIdHistorique() {
		return idHistorique;
	}

	public void setIdHistorique(Long idHistorique) {
		this.idHistorique = idHistorique;
	}

	public Date getDateExecution() {
		return dateExecution;
	}

	public void setDateExecution(Date dateExecution) {
		this.dateExecution = dateExecution;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getCoutMaintenance() {
		return coutMaintenance;
	}

	public void setCoutMaintenance(float coutMaintenance) {
		this.coutMaintenance = coutMaintenance;
	}

	public String getNomTechnicienResponsable() {
		return nomTechnicienResponsable;
	}

	public void setNomTechnicienResponsable(String nomTechnicienResponsable) {
		this.nomTechnicienResponsable = nomTechnicienResponsable;
	}

	public Equipement getEquipement() {
		return equipement;
	}

	public void setEquipement(Equipement equipement) {
		this.equipement = equipement;
	}

	public Technicien getTechnicien() {
		return technicien;
	}

	public void setTechnicien(Technicien technicien) {
		this.technicien = technicien;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}

}
