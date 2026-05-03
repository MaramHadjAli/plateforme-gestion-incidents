package tn.enicarthage.plate_be.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.HashSet;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "badges")
@EqualsAndHashCode(exclude = "badges")
public class Technicien extends Utilisateur {

    @Column(name = "specialite", length = 100)
    private String specialite;

    @Column(name = "tickets_resolus")
    private int ticketsResolus = 0;

    private Double noteMoyenne = 0.0;

    @Column(name = "total_points")
    private int totalPoints = 0;

    @ManyToMany
    @JoinTable(
            name = "technicien_badge",
            joinColumns = @JoinColumn(name = "technicien_id"),
            inverseJoinColumns = @JoinColumn(name = "badge_id")
    )
    private Set<Badge> badges = new HashSet<>();
}
