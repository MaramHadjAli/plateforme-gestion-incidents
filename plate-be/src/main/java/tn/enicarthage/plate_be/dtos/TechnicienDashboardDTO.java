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
    private Long totalTicketsAssigned;
    private Long activeTickets;
    private Long resolvedThisMonth;
    private String averageResolutionTime;
    
    private List<RecentTicketSummary> recentTickets;
    private List<MaintenanceDTO> upcomingMaintenances;
}
