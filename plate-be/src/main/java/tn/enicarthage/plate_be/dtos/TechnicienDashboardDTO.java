package tn.enicarthage.plate_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TechnicienDashboardDTO {
    private Long totalPoints;
    private Double noteMoyenne;
    private Long ticketsResolus;
    private Long totalTicketsAssignes;
    private Long ticketsEnCours;
    private Long ticketsEnRetard;

    private List<RecentTicketSummary> ticketsRecents;
    private List<MaintenanceDTO> maintenancesProches;
}