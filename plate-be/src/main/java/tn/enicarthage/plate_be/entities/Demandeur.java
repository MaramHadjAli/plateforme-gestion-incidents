package tn.enicarthage.plate_be.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@DiscriminatorValue("Demandeur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Demandeur extends Utilisateur {

	private String typeDemandeur;
	private String departement;
	private String numEtudiant;
	private String anneeEtude;
	private String bureau;
	private Double noteMoyenne = 0.0;
	private Integer totalPoint = 0;

	@OneToMany(mappedBy = "demandeur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Feedback> feedbacks;

	@OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Ticket> tickets;

	@OneToMany(mappedBy = "demandeur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<PointTransaction> transactionsPoints;
}