package tn.enicarthage.plate_be.entities;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idTicket;
    private String titre;
    private String description;
    private Date dateCreation;
    private Date dateCloture;

    @Enumerated(EnumType.STRING)
    private PRIORITE_TICKET priorite;
    
    @Enumerated(EnumType.STRING)
    private STATUS_TICKET status;
    
    private Date dateLimiteReparation;
    
    @ManyToOne
    @JoinColumn(name = "demandeur_id")
    private Demandeur createdBy;
    
    @ManyToOne
    @JoinColumn(name = "technicien_id")
    private Technicien assignedTo;
    
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks;
    
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistoriqueMaintenance> historiqueMaintenances;

    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;

    @ManyToOne
    @JoinColumn(name = "equipement_id")
    private Equipement equipement;
}