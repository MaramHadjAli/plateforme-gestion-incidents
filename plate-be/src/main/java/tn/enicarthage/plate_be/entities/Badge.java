package tn.enicarthage.plate_be.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité Badge - Représente les badges que les techniciens peuvent obtenir
 * 
 * Badges disponibles :
 * - Expert : plus de 50 tickets résolus
 * - Rapide : temps moyen < 4 heures
 * - Excellent : satisfaction ≥ 4,5/5
 * - Précis : 20 tickets sans réouverture
 */
@Entity
@Table(name = "badges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class Badge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_badge")
    private String idBadge;
    
    @Column(name = "nom_badge", nullable = false, length = 100)
    private String nomBadge;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @Column(name = "critere_obtention", length = 500)
    private String critereObtention;
    
    @Column(name = "icon", length = 500)
    private String icon; // URL vers l'icône du badge
}