package tn.enicarthage.plate_be.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;
import java.util.HashSet;

/**
 * Entité Technicien - Représente un technicien responsable de la résolution des incidents
 */
@Entity
@Table(name = "techniciens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "badges")
@EqualsAndHashCode(exclude = "badges")
@Builder
public class Technicien extends Utilisateur {
    
    @Column(name = "specialite", length = 100)
    private String specialite;
    
    @Column(name = "tickets_resolus")
    @Builder.Default
    private int ticketsResolus = 0;
    
    @Column(name = "note_moyenne")
    @Builder.Default
    private float noteMoyenne = 0f;
    
    @Column(name = "total_points")
    @Builder.Default
    private int totalPoints = 0;
    
    /**
     * Relation Many-to-Many : Un technicien peut avoir plusieurs badges
     */
    @ManyToMany
    @JoinTable(
        name = "technicien_badge",
        joinColumns = @JoinColumn(name = "technicien_id"),
        inverseJoinColumns = @JoinColumn(name = "badge_id")
    )
    @Builder.Default
    private Set<Badge> badges = new HashSet<>();
}
