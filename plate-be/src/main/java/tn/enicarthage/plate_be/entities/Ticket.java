package tn.enicarthage.plate_be.entities;

import java.util.Date;
import java.util.List;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Builder;

@Entity
@Table(name = "tickets")
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
}