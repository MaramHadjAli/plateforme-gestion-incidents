package tn.enicarthage.plate_be.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Demandeur extends Utilisateur {

	@Enumerated(EnumType.STRING)
	private ROLE typeDemandeur;
	private String departement;
	private String numEtudiant;
	private String anneeEtude;
	private String adresse;
	private float noteMoyenne;
	private int totalPoint;

	@OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Ticket> tickets;

	@OneToMany(mappedBy = "demandeur", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Feedback> feedbacks;

	@OneToMany(mappedBy = "demandeur", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PointTransaction> transactionsPoints;
}
