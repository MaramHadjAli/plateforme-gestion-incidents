package tn.enicarthage.plate_be.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "salles")
public class Salle {

	@Id
	@Column(length = 50)
	private String idSalle;

	@Column(length = 100)
	private String nomSalle;

	@Column(length = 50)
	private String etage;

	@Column(length = 100)
	private String batiment;

	@OneToMany(mappedBy = "salle", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Equipement> equipements;

	public Salle() {
	}


	public String getIdSalle() {
		return idSalle;
	}

	public void setIdSalle(String idSalle) {
		this.idSalle = idSalle;
	}

	public String getNomSalle() {
		return nomSalle;
	}

	public void setNomSalle(String nomSalle) {
		this.nomSalle = nomSalle;
	}

	public String getEtage() {
		return etage;
	}

	public void setEtage(String etage) {
		this.etage = etage;
	}

	public String getBatiment() {
		return batiment;
	}

	public void setBatiment(String batiment) {
		this.batiment = batiment;
	}

	public List<Equipement> getEquipements() {
		return equipements;
	}

	public void setEquipements(List<Equipement> equipements) {
		this.equipements = equipements;
	}
}
