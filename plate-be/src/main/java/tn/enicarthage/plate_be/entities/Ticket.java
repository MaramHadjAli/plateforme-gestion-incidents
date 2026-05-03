package tn.enicarthage.plate_be.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Builder;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ticket")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class Ticket {

    @Id
    private String idTicket;

    private String titre;
    private String description;
    private Date dateCreation;
    private Date dateCloture;
    private Date dateLimiteReparation;

    @Enumerated(EnumType.STRING)
    private PRIORITE_TICKET priorite;

    @Enumerated(EnumType.STRING)
    private STATUS_TICKET status;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private Demandeur createdBy;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private Utilisateur assignedTo;

    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;

    @ManyToOne
    @JoinColumn(name = "equipement_id")
    private Equipement equipement;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PointTransaction> pointTransactions;

    @Column(name = "demande_prix_sent")
    private Boolean demandePrixSent = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "demande_prix_deadline")
    private Date demandePrixDeadline;

    @ManyToMany
    @JoinTable(
            name = "ticket_interested_technicians",
            joinColumns = @JoinColumn(name = "ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "technician_id")
    )
    private List<Utilisateur> interestedTechnicians;

    @ManyToMany
    @JoinTable(
            name = "ticket_declined_technicians",
            joinColumns = @JoinColumn(name = "ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "technician_id")
    )
    private List<Utilisateur> declinedTechnicians;
}