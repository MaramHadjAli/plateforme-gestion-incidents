package tn.enicarthage.plate_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TechnicienDashboardDTO {
    private Long id;
    private String nom;
    private String specialite;
    private Integer totalPoints;
    private Double noteMoyenne;
    private long totalTickets;
    private long enCours;
    private long resolus;
    private long enRetard;
    private double avgResolutionDays;
    private List<RecentTicketSummary> ticketsRecents;
    private List<MaintenanceDTO> maintenancesProchaines;
}