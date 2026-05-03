package tn.enicarthage.plate_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStats {
    private long totalTickets;
    private long openTickets;
    private long inProgressTickets;
    private long resolvedTickets;
    private long overdueTickets;
    private long totalUsers;
    private long totalTechnicians;
    private Map<String, Long> ticketsByStatus;
    private Map<String, Long> ticketsByPriority;
    private List<RecentTicketSummary> recentTickets;
}
