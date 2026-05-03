package tn.enicarthage.plate_be.entities;

import java.util.Date;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Builder;

@Entity
@Table(name = "point_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class PointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idPoint;

    @Column(nullable = false)
    private int quantite;

    @Column(length = 500)
    private String raison;

    @Column(name = "date_attribution")
    private Date dateAttribution;

    @ManyToOne
    @JoinColumn(name = "technicien_id")
    private Technicien technicien;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "demandeur_id")
    private Demandeur demandeur;
}